/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;

/** See https://github.com/handlebars-lang/handlebars.js/blob/master/spec/basic.js */
@DisplayName("basic")
public class BasicTest extends AbstractTest {

  @Test
  public void escaping() throws IOException {
    shouldCompileTo("\\{{foo}}", $("foo", "food"), "{{foo}}");

    shouldCompileTo("content \\{{foo}}", $("foo", "food"), "content {{foo}}");

    shouldCompileTo("\\\\{{foo}}", $("foo", "food"), "\\food");

    shouldCompileTo("content \\\\{{foo}}", $("foo", "food"), "content \\food");

    shouldCompileTo("\\\\ {{foo}}", $("foo", "food"), "\\\\ food");
  }

  @Test
  @DisplayName("compiling with a basic context")
  public void compilingWithBasicContext() throws IOException {
    shouldCompileTo(
        "Goodbye\n{{cruel}}\n{{world}}!",
        $("cruel", "cruel", "world", "world"),
        "Goodbye\ncruel\nworld!");
  }

  @Test
  @DisplayName("compiling with a string context")
  public void compilingWithStringContext() throws IOException {
    shouldCompileTo("{{.}}{{length}}", (Object) "bye", "bye3");
  }

  @Test
  @DisplayName("compiling with an undefined context")
  public void compilingWithUndefinedContext() throws IOException {
    shouldCompileTo("Goodbye\n{{cruel}}\n{{world.bar}}!", (Object) null, "Goodbye\n\n!");
    shouldCompileTo(
        "{{#unless foo}}Goodbye{{../test}}{{test2}}{{/unless}}", (Object) null, "Goodbye");
  }

  @Test
  public void comments() throws IOException {
    shouldCompileTo(
        "{{! Goodbye}}Goodbye\n{{cruel}}\n{{world}}!",
        $("cruel", "cruel", "world", "world"),
        "Goodbye\ncruel\nworld!");

    shouldCompileTo("    {{~! comment ~}}      blah", (Object) null, "blah");

    shouldCompileTo("    {{~!-- long-comment --~}}      blah", (Object) null, "blah");

    shouldCompileTo("    {{! comment ~}}      blah", (Object) null, "    blah");

    shouldCompileTo("    {{!-- long-comment --~}}      blah", (Object) null, "    blah");

    shouldCompileTo("    {{~! comment}}      blah", (Object) null, "      blah");

    shouldCompileTo("    {{~!-- long-comment --}}      blah", (Object) null, "      blah");

    shouldCompileTo("{{!-- This comment may contain mustaches {{ like }} --}}", (Object) null, "");
  }

  @Test
  @DisplayName("booleans show the contents when true")
  public void boolTrue() throws IOException {
    String string = "{{#goodbye}}GOODBYE {{/goodbye}}cruel {{world}}!";
    shouldCompileTo(string, $("goodbye", true, "world", "world"), "GOODBYE cruel world!");

    shouldCompileTo(string, $("goodbye", false, "world", "world"), "cruel world!");
  }

  @Test
  @DisplayName("booleans do not show the contents when false")
  public void boolFalse() throws IOException {
    String string = "{{#goodbye}}GOODBYE {{/goodbye}}cruel {{world}}!";
    shouldCompileTo(string, $("goodbye", false, "world", "world"), "cruel world!");
  }

  @Test
  public void zeros() throws IOException {
    shouldCompileTo(
        "num1: {{num1}}, num2: {{num2}}", $("num1", 42, "num2", 0), "num1: 42, num2: 0");

    shouldCompileTo("num: {{.}}", (Object) 0, "num: 0");

    shouldCompileTo("num: {{num1/num2}}", $("num1", $("num2", 0)), "num: 0");
  }

  @Test
  public void falsy() throws IOException {
    shouldCompileTo(
        "val1: {{val1}}, val2: {{val2}}",
        $("val1", false, "val2", Boolean.FALSE),
        "val1: false, val2: false");

    shouldCompileTo("val: {{.}}", (Object) false, "val: false");

    shouldCompileTo("val: {{val1/val2}}", $("val1", $("val2", false)), "val: false");

    shouldCompileTo(
        "val1: {{{val1}}}, val2: {{{val2}}}",
        $("val1", false, "val2", Boolean.FALSE),
        "val1: false, val2: false");

    shouldCompileTo("val: {{{val1/val2}}}", $("val1", $("val2", false)), "val: false");
  }

  @Test
  @DisplayName("escaping text")
  public void escapingText() throws IOException {
    shouldCompileTo("Awesome's", (Object) null, "Awesome's");

    shouldCompileTo("Awesome\\", (Object) null, "Awesome\\");

    shouldCompileTo("Awesome\\\\ foo", (Object) null, "Awesome\\\\ foo");

    shouldCompileTo("Awesome {{foo}}", $("foo", "\\"), "Awesome \\");

    shouldCompileTo(" ' ' ", (Object) null, " ' ' ");
  }

  @Test
  @DisplayName("escaping expressions")
  public void escapingExpressions() throws IOException {
    shouldCompileTo("{{{awesome}}}", $("awesome", "&'\\<>"), "&'\\<>");

    shouldCompileTo("{{&awesome}}", $("awesome", "&'\\<>"), "&'\\<>");

    shouldCompileTo("{{awesome}}", $("awesome", "&\"'`\\<>"), "&amp;&quot;&#x27;&#x60;\\&lt;&gt;");

    shouldCompileTo(
        "{{awesome}}",
        $("awesome", "Escaped, <b> looks like: &lt;b&gt;"),
        "Escaped, &lt;b&gt; looks like: &amp;lt;b&amp;gt;");

    shouldCompileTo(
        "{{hello.bar.ff}}",
        $("hello.bar.ff", "ignored", "hello", $("bar", $("ff", "nested"))),
        "nested");
  }

  @Test
  public void newlines() throws IOException {
    shouldCompileTo("Alan's\nTest", (Object) null, "Alan's\nTest");

    shouldCompileTo("Alan's\rTest", (Object) null, "Alan's\rTest");
  }

  public static class SafeStringModel {
    public CharSequence getAwesome() {
      return new Handlebars.SafeString("&'\\<>");
    }
  }

  @Test
  @DisplayName("functions returning safestrings shouldn't be escaped")
  public void shouldNotEscapeSafeStrings() throws IOException {
    shouldCompileTo("{{awesome}}", new SafeStringModel(), "&'\\<>");
  }

  public static class Fun0 {
    public String getAwesome() {
      return "Awesome";
    }
  }

  public static class Fun00 {
    private final String more;

    public Fun00(final String more) {
      this.more = more;
    }

    public String getAwesome() {
      return this.more;
    }
  }

  @Test
  @DisplayName("functions are called and render their output")
  public void functions() throws IOException {
    shouldCompileTo("{{awesome}}", new Fun0(), "Awesome");

    shouldCompileTo("{{awesome}}", new Fun00("More Awesome"), "More Awesome");
  }

  @Test
  @DisplayName("functions with context argument")
  public void functionsWithContextArgument() throws IOException {
    Helper<Object> awesome = (context, options) -> context;
    shouldCompileTo("{{awesome frank}}", $("frank", "Frank"), $("awesome", awesome), "Frank");

    Helper<Object> frank = (context, options) -> "should NOT OVERRIDE";
    shouldCompileTo(
        "{{awesome frank}}", $("frank", "Frank"), $("awesome", awesome, "frank", frank), "Frank");
  }

  public static class FunCtx06 {
    public FunCtx06 getAwesome() {
      return this;
    }
  }

  public static class FunCtx07 {
    private final FunCtx06 foo;

    public FunCtx07(final FunCtx06 foo) {
      this.foo = foo;
    }

    public FunCtx06 getFoo() {
      return foo;
    }
  }

  @Test
  @DisplayName("pathed block functions without context argument")
  public void pathedBlockFunctionsWithoutContextArgument() throws IOException {
    shouldCompileTo("{{#foo.awesome}}inner{{/foo.awesome}}", new FunCtx07(new FunCtx06()), "inner");
  }

  public static class FunCtx08 {
    public boolean getValue() {
      return true;
    }

    public FunCtx08 getAwesome() {
      return this;
    }
  }

  @Test
  @DisplayName("depthed block functions without context argument")
  public void depthedBlockFunctionsWithoutContextArgument() throws IOException {
    shouldCompileTo(
        "{{#with value}}{{#../awesome}}inner{{/../awesome}}{{/with}}", new FunCtx08(), "inner");
  }

  @Test
  @DisplayName("paths with hyphens")
  public void pathsWithHyphens() throws IOException {
    shouldCompileTo("{{foo-bar}}", $("foo-bar", "baz"), "baz");

    shouldCompileTo("{{foo.foo-bar}}", $("foo", $("foo-bar", "baz")), "baz");

    shouldCompileTo("{{foo/foo-bar}}", $("foo", $("foo-bar", "baz")), "baz");
  }

  @Test
  @DisplayName("nested paths")
  public void nestedPaths() throws IOException {
    shouldCompileTo(
        "Goodbye {{alan/expression}} world!",
        $("alan", $("expression", "beautiful")),
        "Goodbye beautiful world!");
  }

  @Test
  @DisplayName("nested paths with empty string value")
  public void nestedPathsWithEmptyString() throws IOException {
    shouldCompileTo(
        "Goodbye {{alan/expression}} world!", $("alan", $("expression", "")), "Goodbye  world!");
  }

  @Test
  @DisplayName("literal paths")
  public void literalPaths() throws IOException {
    shouldCompileTo(
        "Goodbye {{[@alan]/expression}} world!",
        $("@alan", $("expression", "beautiful")),
        "Goodbye beautiful world!");

    shouldCompileTo(
        "Goodbye {{[foo bar]/expression}} world!",
        $("foo bar", $("expression", "beautiful")),
        "Goodbye beautiful world!");
  }

  @Test
  @DisplayName("literal references")
  public void literalReferences() throws IOException {
    shouldCompileTo(
        "Goodbye {{[foo bar]}} world!", $("foo bar", "beautiful"), "Goodbye beautiful world!");

    shouldCompileTo(
        "Goodbye {{\"foo bar\"}} world!", $("foo bar", "beautiful"), "Goodbye beautiful world!");

    shouldCompileTo(
        "Goodbye {{'foo bar'}} world!", $("foo bar", "beautiful"), "Goodbye beautiful world!");

    shouldCompileTo(
        "Goodbye {{\"foo'bar\"}} world!", $("foo'bar", "beautiful"), "Goodbye beautiful world!");

    shouldCompileTo(
        "Goodbye {{'foo\"bar'}} world!", $("foo\"bar", "beautiful"), "Goodbye beautiful world!");
  }

  @Test
  @DisplayName("literal reference containing an opening bracket")
  public void literalReferenceContainingBracket() throws IOException {
    shouldCompileTo(
        "Goodbye {{\"foo[bar\"}} world!", $("foo[bar", "beautiful"), "Goodbye beautiful world!");
  }

  @Test
  @DisplayName("that current context path ({{.}}) doesn't hit helpers")
  public void thatCurrentContextPathDoesntHitHelpers() throws IOException {
    Helper<Object> helper = (context, options) -> "awseome";
    shouldCompileTo("test: {{.}}", (Object) null, $("helper", helper), "test: ");
  }

  @Test
  @DisplayName("complex but empty paths")
  public void complexButEmptyPaths() throws IOException {
    shouldCompileTo("{{person/name}}", $("person", $("name", null)), "");

    shouldCompileTo("{{person/name}}", $("person", $()), "");
  }

  public static class StaticHello {
    private final String text;

    public StaticHello(final String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }
  }

  public static class StaticHellos {
    private final List<StaticHello> hellos;

    public StaticHellos(final List<StaticHello> hellos) {
      this.hellos = hellos;
    }

    public List<StaticHello> getHellos() {
      return hellos;
    }
  }

  private static StaticHellos staticHellos() {
    return new StaticHellos(
        List.of(new StaticHello("hello"), new StaticHello("Hello"), new StaticHello("HELLO")));
  }

  @Test
  @DisplayName("this keyword in paths")
  public void thisKeywordInPaths() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{this}}{{/goodbyes}}",
        $("goodbyes", new String[] {"goodbye", "Goodbye", "GOODBYE"}),
        "goodbyeGoodbyeGOODBYE");

    shouldCompileTo(
        "{{#hellos}}{{this/text}}{{/hellos}}",
        $("hellos", List.of($("text", "hello"), $("text", "Hello"), $("text", "HELLO"))),
        "helloHelloHELLO");

    // Static as dynamic :S
    shouldCompileTo("{{#hellos}}{{this/text}}{{/hellos}}", staticHellos(), "helloHelloHELLO");

    // Truly Static :)
    shouldCompileTo("{{#hellos}}{{this/text}}{{/hellos}}", staticHellos(), "helloHelloHELLO");
  }

  @Test
  @DisplayName("this keyword nested inside path")
  public void thisKeywordNestedInsidePath() throws IOException {
    assertThrows(
        HandlebarsException.class,
        () -> compile("{{#hellos}}{{text/this/foo}}{{/hellos}}").apply((Object) null));

    shouldCompileTo("{{[this]}}", $("this", "bar"), "bar");

    shouldCompileTo("{{text/[this]}}", $("text", $("this", "bar")), "bar");
  }

  @Test
  @DisplayName("this keyword in helpers")
  public void thisKeywordInHelpers() throws IOException {
    Helper<Object> foo = (context, options) -> "bar " + context;

    shouldCompileTo(
        "{{#goodbyes}}{{foo this}}{{/goodbyes}}",
        $("goodbyes", new String[] {"goodbye", "Goodbye", "GOODBYE"}),
        $("foo", foo),
        "bar goodbyebar Goodbyebar GOODBYE");

    shouldCompileTo(
        "{{#hellos}}{{foo this.text}}{{/hellos}}",
        staticHellos(),
        $("foo", foo),
        "bar hellobar Hellobar HELLO");

    shouldCompileTo(
        "{{#hellos}}{{foo this.text}}{{/hellos}}",
        staticHellos(),
        $("foo", foo),
        "bar hellobar Hellobar HELLO");
  }

  @Test
  @DisplayName("this keyword nested inside helpers param")
  public void thisKeywordNestedInsideHelpersParam() throws IOException {
    assertThrows(
        HandlebarsException.class,
        () -> compile("\n{{#hellos}}\n{{foo text/this/foo}}\n{{/hellos}}").apply((Object) null));
  }

  @Test
  @DisplayName("pass string literals")
  public void passStringLiterals() throws IOException {
    shouldCompileTo("{{\"foo\"}}", (Object) null, "");

    shouldCompileTo("{{\"foo\"}}", $("foo", "bar"), "bar");

    shouldCompileTo("{{#\"foo\"}}{{.}}{{/\"foo\"}}", $("foo", List.of("bar", "baz")), "barbaz");
  }

  @Test
  @DisplayName("pass number literals")
  public void passNumberLiterals() throws IOException {
    shouldCompileTo("{{12}}", (Object) null, "");
    shouldCompileTo("{{12}}", $("12", "bar"), "bar");
  }

  @Test
  @DisplayName("pass decimal number literals")
  public void passDecimalNumberLiterals() throws IOException {
    shouldCompileTo("{{12.34}}", (Object) null, "");
    shouldCompileTo("{{12.34}}", $("12.34", "bar"), "bar");
  }

  @Test
  @DisplayName("pass boolean literals")
  public void passBooleanLiterals() throws IOException {
    shouldCompileTo("{{true}}", (Object) null, "");
    shouldCompileTo("{{true}}", $("", "foo"), "");
    shouldCompileTo("{{false}}", $("false", "foo"), "foo");
  }

  public static class FunCtx11 {
    public String getFalse() {
      return "bar";
    }
  }

  @Test
  @DisplayName("should handle literals in subexpression")
  public void shouldHandleLiteralsInSubexpression() throws IOException {
    Helper<Object> foo = (context, options) -> context;

    shouldCompileTo("{{foo (false)}}", $("false", "bar"), $("foo", foo), "bar");

    shouldCompileTo("{{foo (false)}}", new FunCtx11(), $("foo", foo), "bar");

    shouldCompileTo("{{foo (false)}}", new FunCtx11(), $("foo", foo), "bar");
  }
}
