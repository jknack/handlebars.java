/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/subexpressions.js */
@DisplayName("subexpressions")
public class SubexpressionsTest extends AbstractTest {

  @Test
  @DisplayName("arg-less helper")
  public void arglesshelper() throws IOException {
    shouldCompileTo(
        "{{foo (bar)}}!",
        $(),
        $(
            "foo",
            (Helper<Object>) (context, options) -> context.toString() + context,
            "bar",
            (Helper<Object>) (context, options) -> "LOL"),
        "LOLLOL!");
  }

  @Test
  @DisplayName("helper w args")
  public void helperwargs() throws IOException {
    Hash helpers =
        $(
            "blog",
            (Helper<Object>) (context, options) -> "val is " + context,
            "equal",
            (Helper<Object>) (context, options) -> Objects.equals(context, options.param(0)));
    shouldCompileTo("{{blog (equal a b)}}", $("bar", "LOL"), helpers, "val is true", "");
    shouldCompileTo(
        "{{blog (equal a b)}}",
        $("bar", "LOL"),
        $(
            "blog",
            (Helper<Object>) (context, options) -> Sub00.blog(context),
            "equal",
            (Helper<Object>) (context, options) -> Sub00.equal(context, options.param(0))),
        "val is true",
        "");
  }

  public static class Sub00 {
    public static boolean equal(Object a, Object b) {
      return Objects.equals(a, b);
    }

    public static String blog(Object value) {
      return "val is " + value;
    }
  }

  public static class Sub01 {
    public static boolean equal(Object a, Object b) {
      return Objects.equals(a, b);
    }

    public static String blog(Object value, Object that, Object theOther) {
      return "val is " + value + ", " + that + " and " + theOther;
    }
  }

  @Test
  @DisplayName("mixed paths and helpers")
  public void mixedpathsandhelpers() throws IOException {
    Object input = $("bar", "LOL", "baz", $("bat", "foo!", "bar", "bar!"));
    Helper<Object> blog =
        (context, options) ->
            "val is " + context + ", " + options.param(0) + " and " + options.param(1);
    Helper<Object> equal = (context, options) -> Objects.equals(context, options.param(0));
    shouldCompileTo(
        "{{blog baz.bat (equal a b) baz.bar}}",
        input,
        $("blog", blog, "equal", equal),
        "val is foo!, true and bar!",
        "");
    shouldCompileTo(
        "{{blog baz.bat (eq a b) baz.bar}}",
        input,
        $(
            "blog",
            blog,
            "eq",
            (Helper<Object>) (context, options) -> Objects.equals(context, options.param(0))),
        "val is foo!, true and bar!",
        "");
    shouldCompileTo(
        "{{blog baz.bat (equal a b) baz.bar}}",
        input,
        $(
            "blog",
                (Helper<Object>)
                    (context, options) -> Sub01.blog(context, options.param(0), options.param(1)),
            "equal", (Helper<Object>) (context, options) -> Sub01.equal(context, options.param(0))),
        "val is foo!, true and bar!",
        "");
    shouldCompileTo(
        "{{blog baz.bat (equal a b) baz.bar}}",
        new Model01("LOL", new Model00("foo!", "bar!")),
        $(
            "blog",
                (Helper<Object>)
                    (context, options) -> Sub01.blog(context, options.param(0), options.param(1)),
            "equal", (Helper<Object>) (context, options) -> Sub01.equal(context, options.param(0))),
        "val is foo!, true and bar!",
        "");
  }

  public static class Model00 {
    private final String bat;
    private final String bar;

    public Model00(String bat, String bar) {
      this.bat = bat;
      this.bar = bar;
    }

    public String getBat() {
      return bat;
    }

    public String getBar() {
      return bar;
    }
  }

  public static class Model01 {
    private final String bar;
    private final Model00 baz;

    public Model01(String bar, Model00 baz) {
      this.bar = bar;
      this.baz = baz;
    }

    public String getBar() {
      return bar;
    }

    public Model00 getBaz() {
      return baz;
    }
  }

  @Test
  @DisplayName("supports much nesting")
  public void supportsmuchnesting() throws IOException {
    shouldCompileTo(
        "{{blog (equal (equal true true) true)}}",
        $("bar", "LOL"),
        $(
            "blog",
            (Helper<Object>) (context, options) -> Sub00.blog(context),
            "equal",
            (Helper<Object>) (context, options) -> Sub00.equal(context, options.param(0))),
        "val is true",
        "");
    shouldCompileTo(
        "{{blog (eq (eq true true) true)}}",
        $("bar", "LOL"),
        $(
            "blog", (Helper<Object>) (context, options) -> "val is " + context,
            "eq", (Helper<Object>) (context, options) -> Objects.equals(context, options.param(0))),
        "val is true",
        "");
    shouldCompileTo(
        "{{blog (equal (equal true true) true)}}",
        $("bar", "LOL"),
        $(
            "blog",
            (Helper<Object>) (context, options) -> Sub00.blog(context),
            "equal",
            (Helper<Object>) (context, options) -> Sub00.equal(context, options.param(0))),
        "val is true",
        "");
  }

  @Test
  @DisplayName("Complex subexpressions")
  public void complexsubexpressions() throws IOException {
    Object context = $("a", "a", "b", "b", "c", $("c", "c"), "d", "d", "e", $("e", "e"));
    Map<String, Helper<Object>> helpers =
        Map.of(
            "dash", (ctx, options) -> ctx + "-" + options.param(0),
            "concat", (ctx, options) -> ctx.toString() + options.param(0));
    shouldCompileTo(
        "{{dash 'abc' (concat a b)}}",
        context,
        $("dash", helpers.get("dash"), "concat", helpers.get("concat")),
        "abc-ab",
        "");
    shouldCompileTo(
        "{{dash d (concat a b)}}",
        context,
        $("dash", helpers.get("dash"), "concat", helpers.get("concat")),
        "d-ab",
        "");
    shouldCompileTo(
        "{{dash c.c (concat a b)}}",
        context,
        $("dash", helpers.get("dash"), "concat", helpers.get("concat")),
        "c-ab",
        "");
    shouldCompileTo(
        "{{dash (concat a b) c.c}}",
        context,
        $("dash", helpers.get("dash"), "concat", helpers.get("concat")),
        "ab-c",
        "");
    shouldCompileTo(
        "{{dash (concat a e.e) c.c}}",
        context,
        $("dash", helpers.get("dash"), "concat", helpers.get("concat")),
        "ae-c",
        "");
  }

  @Test
  @DisplayName("provides each nested helper invocation its own options hash")
  public void provideseachnestedhelperinvocationitsownoptionshash() throws IOException {
    HashSet<Options> uniqueOptions = new HashSet<>();
    shouldCompileTo(
        "{{equal (equal true true) true}}",
        $(),
        $(
            "equal",
            (Helper<Object>)
                (context, options) -> {
                  if (!uniqueOptions.add(options)) {
                    throw new IllegalStateException("Duplicate options hash");
                  }
                  return Objects.equals(context, options.param(0));
                }),
        "true",
        "");
  }

  @Test
  @DisplayName("with hashes")
  public void withhashes() throws IOException {
    shouldCompileTo(
        "{{blog (equal (equal true true) true fun='yes')}}",
        $("bar", "LOL"),
        $(
            "blog",
            (Helper<Object>) (context, options) -> "val is " + context,
            "equal",
            (Helper<Object>) (context, options) -> Objects.equals(context, options.param(0))),
        "val is true",
        "");
  }

  public static class Sub02 {
    public static boolean equal(Object a, Object b) {
      return Objects.equals(a, b);
    }

    public static String blog(Object fun) {
      return "val is " + fun;
    }
  }

  @Test
  @DisplayName("as hashes")
  public void ashashes() throws IOException {
    shouldCompileTo(
        "{{blog fun=(equal (blog fun=1) 'val is 1')}}",
        $(),
        $(
            "blog",
            (Helper<Object>) (context, options) -> "val is " + options.hash("fun"),
            "equal",
            (Helper<Object>) (context, options) -> Objects.equals(context, options.param(0))),
        "val is true",
        "");
    shouldCompileTo(
        "{{blog fun=(equal (blog fun=1) 'val is 1')}}",
        $(),
        $(
            "blog", (Helper<Object>) (context, options) -> Sub02.blog(options.hash("fun")),
            "equal", (Helper<Object>) (context, options) -> Sub02.equal(context, options.param(0))),
        "val is true",
        "");
  }

  @Test
  @DisplayName("multiple subexpressions in a hash")
  public void multiplesubexpressionsinahash() throws IOException {
    shouldCompileTo(
        "{{input aria-label=(t \"Name\") placeholder=(t \"Example User\")}}",
        $(),
        $(
            "input",
                (Helper<Object>)
                    (context, options) -> {
                      CharSequence ariaLabel =
                          Handlebars.Utils.escapeExpression(
                              (CharSequence) options.hash("aria-label"));
                      CharSequence placeholder =
                          Handlebars.Utils.escapeExpression(
                              (CharSequence) options.hash("placeholder"));
                      return new Handlebars.SafeString(
                          "<input aria-label=\""
                              + ariaLabel
                              + "\" placeholder=\""
                              + placeholder
                              + "\" />");
                    },
            "t",
                (Helper<Object>)
                    (context, options) -> new Handlebars.SafeString(context.toString())),
        "<input aria-label=\"Name\" placeholder=\"Example User\" />",
        "");
  }

  @Test
  @DisplayName("multiple subexpressions in a hash with context")
  public void multiplesubexpressionsinahashwithcontext() throws IOException {
    shouldCompileTo(
        "{{input aria-label=(t item.field) placeholder=(t item.placeholder)}}",
        $("item", $("field", "Name", "placeholder", "Example User")),
        $(
            "input",
                (Helper<Object>)
                    (context, options) -> {
                      CharSequence ariaLabel =
                          Handlebars.Utils.escapeExpression(
                              (CharSequence) options.hash("aria-label"));
                      CharSequence placeholder =
                          Handlebars.Utils.escapeExpression(
                              (CharSequence) options.hash("placeholder"));
                      return new Handlebars.SafeString(
                          "<input aria-label=\""
                              + ariaLabel
                              + "\" placeholder=\""
                              + placeholder
                              + "\" />");
                    },
            "t",
                (Helper<Object>)
                    (context, options) -> new Handlebars.SafeString(context.toString())),
        "<input aria-label=\"Name\" placeholder=\"Example User\" />",
        "");
  }

  public static class Model02 {
    public String getBar() {
      return "LOL";
    }
  }

  @Test
  @DisplayName("subexpression functions on the context")
  public void subexpressionfunctionsonthecontext() throws IOException {
    Helper<Object> foo = (context, options) -> context.toString() + context;
    Helper<Object> bar = (context, options) -> ((Model02) context).getBar();
    shouldCompileTo("{{foo (bar)}}!", new Model02(), $("foo", foo, "bar", bar), "LOLLOL!", "");
    shouldCompileTo("{{foo (bar)}}!", new Model02(), $("foo", foo, "bar", bar), "LOLLOL!", "");
  }
}
