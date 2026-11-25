/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/strict.js */
@DisplayName("strict")
public class StrictTest extends AbstractTest {

  private static final String NO_STRICT = "no equivalent API: strict mode is not supported";

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
  @Disabled(NO_STRICT)
  @DisplayName("should error on missing property lookup")
  public void shoulderroronmissingpropertylookup() throws IOException {}

  @Test
  @Disabled(NO_STRICT)
  @DisplayName("should error on missing child")
  public void shoulderroronmissingchild() throws IOException {}

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
  @Disabled(NO_STRICT)
  @DisplayName("should error on missing property lookup in known helpers mode")
  public void shoulderroronmissingpropertylookupinknownhelpersmode() throws IOException {}

  @Test
  @Disabled(NO_STRICT)
  @DisplayName("should error on missing data lookup")
  public void shoulderroronmissingdatalookup() throws IOException {}

  @Test
  @Disabled(NO_STRICT)
  @DisplayName("should not run helperMissing for helper calls")
  public void shouldnotrunhelperMissingforhelpercalls() throws IOException {}

  @Test
  @Disabled(NO_STRICT)
  @DisplayName("should throw on ambiguous blocks")
  public void shouldthrowonambiguousblocks() throws IOException {}

  @Test
  @DisplayName("should allow undefined parameters when passed to helpers")
  public void shouldallowundefinedparameterswhenpassedtohelpers() throws IOException {
    shouldCompileTo("{{#unless foo}}success{{/unless}}", (Object) null, "success");
  }

  @Test
  @Disabled(NO_STRICT)
  @DisplayName("should allow undefined hash when passed to helpers")
  public void shouldallowundefinedhashwhenpassedtohelpers() throws IOException {}

  @Test
  @Disabled(NO_STRICT)
  @DisplayName("should show error location on missing property lookup")
  public void shouldshowerrorlocationonmissingpropertylookup() throws IOException {}

  @Test
  @Disabled("no equivalent API: strict and compat modes are not supported")
  @DisplayName("strict and compat mode")
  public void strictandcompatmode() throws IOException {}

  @Test
  @DisplayName("assume objects")
  public void assumeobjects() throws IOException {
    shouldCompileTo("{{hello}}", (Object) null, "");

    shouldCompileTo("{{hello.bar}}", $("hello", $()), "");

    shouldCompileTo("{{hello.bar}}", new Strict04(new Strict03("x")), "x");
  }
}
