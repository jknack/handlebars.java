/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/strict.js */
@DisplayName("strict")
public class StrictTest extends AbstractTest {

  private boolean strict = true;

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.strict(strict);
  }

  private void assertUndefined(final String template, final Object data, final String name) {
    HandlebarsException ex =
        assertThrows(HandlebarsException.class, () -> shouldCompileTo(template, data, ""));
    assertTrue(ex.getMessage().contains("'" + name + "' not defined"), ex.getMessage());
  }

  public static class Strict03 {
    private final String bar;

    public Strict03(final String bar) {
      this.bar = bar;
    }

    public String getBar() {
      return bar;
    }
  }

  public static class Strict04 {
    private final Strict03 hello;

    public Strict04(final Strict03 hello) {
      this.hello = hello;
    }

    public Strict03 getHello() {
      return hello;
    }
  }

  @Test
  @DisplayName("should error on missing property lookup")
  public void shoulderroronmissingpropertylookup() throws IOException {
    assertUndefined("{{hello}}", (Object) null, "hello");
    assertUndefined("{{hello}}", $(), "hello");
    assertUndefined("{{bar}}\n{{hello}}", new Strict03("foo"), "hello");
  }

  @Test
  @DisplayName("should error on missing child")
  public void shoulderroronmissingchild() throws IOException {
    shouldCompileTo("{{hello.bar}}", $("hello", $("bar", "foo")), "foo");
    assertUndefined("{{hello.bar}}", $("hello", $()), "bar");
    assertUndefined("{{hello.bar}}", $(), "hello");
    shouldCompileTo("{{hello.bar}}", new Strict04(null), "");
  }

  @Test
  @DisplayName("should handle array length")
  public void shouldhandlearraylength() throws IOException {
    Map<String, Object> hash = new HashMap<>();
    hash.put("hello", List.of(1, 2, 3));
    shouldCompileTo("{{hello.size}}", hash, "3");
  }

  @Test
  @DisplayName("should handle string length")
  public void shouldhandlestringlength() throws IOException {
    shouldCompileTo("{{hello.length}}", $("hello", "world"), "5");
  }

  @Test
  @DisplayName("should error on missing property lookup in known helpers mode")
  public void shoulderroronmissingpropertylookupinknownhelpersmode() throws IOException {
    // there is no known-helpers-only mode: unknown helpers are never invoked as helpers
    assertUndefined("{{hello}}", (Object) null, "hello");
  }

  @Test
  @DisplayName("should error on missing data lookup")
  public void shoulderroronmissingdatalookup() throws IOException {
    assertUndefined("{{@hello}}", (Object) null, "hello");
    HandlebarsException ex =
        assertThrows(
            HandlebarsException.class,
            () -> compile("{{@hello.bar}}").apply(Context.newContext($()).data("hello", $())));
    assertTrue(ex.getMessage().contains("'bar' not defined"), ex.getMessage());
  }

  @Test
  @DisplayName("should not run helperMissing for helper calls")
  public void shouldnotrunhelperMissingforhelpercalls() throws IOException {
    // unknown helpers (with arguments) are rejected at compile time, helperMissing isn't needed
    HandlebarsException ex =
        assertThrows(
            HandlebarsException.class, () -> shouldCompileTo("{{hello foo}}", $("foo", "bar"), ""));
    assertTrue(ex.getMessage().contains("'hello'"), ex.getMessage());
  }

  @Test
  @DisplayName("should throw on ambiguous blocks")
  public void shouldthrowonambiguousblocks() throws IOException {
    assertUndefined("{{#hello}}{{/hello}}", (Object) null, "hello");
    assertUndefined("{{^hello}}{{/hello}}", (Object) null, "hello");
    assertUndefined("{{#hello.bar}}{{/hello.bar}}", $("hello", $()), "bar");
  }

  @Test
  @DisplayName("should allow undefined parameters when passed to helpers")
  public void shouldallowundefinedparameterswhenpassedtohelpers() throws IOException {
    shouldCompileTo("{{#unless foo}}success{{/unless}}", (Object) null, "success");
  }

  @Test
  @DisplayName("should allow undefined hash when passed to helpers")
  public void shouldallowundefinedhashwhenpassedtohelpers() throws IOException {
    shouldCompileTo(
        "{{helper value=@foo}}",
        (Object) null,
        $(
            "helper",
            (Helper<Object>)
                (ctx, options) -> options.hash("value") == null ? "success" : "failure"),
        "success");
  }

  @Test
  @DisplayName("should show error location on missing property lookup")
  public void shouldshowerrorlocationonmissingpropertylookup() throws IOException {
    HandlebarsException ex =
        assertThrows(
            HandlebarsException.class, () -> shouldCompileTo("\n\n\n   {{hello}}", $(), ""));
    assertTrue(ex.getMessage().contains(":4:"), ex.getMessage());
    assertTrue(ex.getMessage().contains("'hello' not defined"), ex.getMessage());
  }

  @Test
  @DisplayName("strict and compat mode")
  public void strictandcompatmode() throws IOException {
    shouldCompileTo("{{v}}", $("v", "a"), "a");
    assertUndefined("{{v}}", $(), "v");
    shouldCompileTo("{{#foo}}Hello {{bar}}{{/foo}}", $("foo", true, "bar", "World"), "Hello World");
    assertUndefined("{{#each items}}{{name}}{{/each}}", $("items", List.of(1, 2)), "name");
    shouldCompileTo(
        "{{#each items}}{{name}}{{/each}}", $("name", "root", "items", List.of($())), "root");
    shouldCompileTo(
        "{{#with child}}{{name.first}}{{/with}}",
        $("name", $("first", "root"), "child", $("x", "y")),
        "root");
  }

  @Test
  @DisplayName("assume objects")
  public void assumeobjects() throws IOException {
    strict = false;
    shouldCompileTo("{{hello}}", (Object) null, "");

    shouldCompileTo("{{hello.bar}}", $("hello", $()), "");

    shouldCompileTo("{{hello.bar}}", new Strict04(new Strict03("x")), "x");
  }
}
