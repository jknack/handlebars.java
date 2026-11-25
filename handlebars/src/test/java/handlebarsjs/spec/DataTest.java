/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/data.js */
@DisplayName("data")
public class DataTest extends AbstractTest {

  /** Sets hash values as data for the block, restoring previous values afterwards. */
  private static final Helper<Object> LET =
      (context, options) -> {
        Map<String, Object> previous = new HashMap<>();
        for (Map.Entry<String, Object> entry : options.hash.entrySet()) {
          previous.put(entry.getKey(), options.data(entry.getKey()));
          options.data(entry.getKey(), entry.getValue());
        }
        try {
          return options.fn(context);
        } finally {
          for (Map.Entry<String, Object> entry : previous.entrySet()) {
            options.data(entry.getKey(), entry.getValue());
          }
        }
      };

  private static final Helper<Object> HELLO_NOUN_DATA =
      (context, options) -> options.data("adjective") + " " + options.get("noun");

  private static final Helper<Object> HELLO_PARAM = (noun, options) -> "Hello " + noun;

  private static Context newContext(final Object model, final Map<String, Object> data) {
    return Context.newContext(model).data(data);
  }

  private static Map<String, Object> data(final Object... attributes) {
    return $(attributes);
  }

  private String render(final String template, final Hash helpers, final Context context)
      throws IOException {
    return compile(template, helpers).apply(context);
  }

  @Test
  @DisplayName("passing in data to a compiled function that expects data - works with helpers")
  public void passingindatatoacompiledfunctionthatexpectsdataworkswithhelpers() throws IOException {
    String result =
        render(
            "{{hello}}",
            $("hello", HELLO_NOUN_DATA),
            newContext($("noun", "cat"), data("adjective", "happy")));
    assertEquals("happy cat", result);
  }

  @Test
  @DisplayName("data can be looked up via @foo")
  public void datacanbelookedupviafoo() throws IOException {
    String result =
        render("{{@hello}}", $("hello", HELLO_NOUN_DATA), newContext($(), data("hello", "hello")));
    assertEquals("hello", result);
  }

  @Test
  @DisplayName("deep @foo triggers automatic top-level data")
  public void deeptriggersautomatictopleveldata() throws IOException {
    String result =
        render(
            "{{#let world=\"world\"}}{{#if foo}}{{#if foo}}Hello {{@world}}{{/if}}{{/if}}{{/let}}",
            $("let", LET),
            newContext($("foo", true), data()));
    assertEquals("Hello world", result);
  }

  @Test
  @DisplayName("parameter data can be looked up via @foo")
  public void parameterdatacanbelookedupviafoo() throws IOException {
    String result =
        render(
            "{{hello @world}}", $("hello", HELLO_PARAM), newContext($(), data("world", "world")));
    assertEquals("Hello world", result);
  }

  @Test
  @DisplayName("hash values can be looked up via @foo")
  public void hashvaluescanbelookedupviafoo() throws IOException {
    Helper<Object> hello = (context, options) -> "Hello " + options.hash("noun");
    String result =
        render(
            "{{ hello noun=@world }}", $("hello", hello), newContext($(), data("world", "world")));
    assertEquals("Hello world", result);
  }

  @Test
  @DisplayName("nested parameter data can be looked up via @foo.bar")
  public void nestedparameterdatacanbelookedupviafoobar() throws IOException {
    String result =
        render(
            "{{hello @world.bar}}",
            $("hello", HELLO_PARAM),
            newContext($(), data("world", $("bar", "world"))));
    assertEquals("Hello world", result);
  }

  @Test
  @DisplayName("nested parameter data does not fail with @world.bar")
  public void nestedparameterdatadoesnotfailwithworldbar() throws IOException {
    String result =
        render(
            "{{hello @world.bar}}",
            $("hello", HELLO_PARAM),
            newContext($(), data("foo", $("bar", "world"))));
    assertEquals("Hello null", result);
  }

  @Test
  @DisplayName("parameter data throws when using complex scope references")
  public void parameterdatathrowswhenusingcomplexscopereferences() throws IOException {
    assertThrows(
        HandlebarsException.class,
        () -> compile("{{#goodbyes}}{{text}} cruel {{@foo/../name}}! {{/goodbyes}}").apply($()));
  }

  @Test
  @DisplayName("data is inherited downstream")
  public void dataisinheriteddownstream() throws IOException {
    String result =
        render(
            "{{#let foo=1 bar=2}}{{#let foo=bar.baz}}{{@bar}}{{@foo}}{{/let}}{{@foo}}{{/let}}",
            $("let", LET),
            newContext($("bar", $("baz", "hello world")), data()));
    assertEquals("2hello world1", result);
  }

  @Test
  @DisplayName(
      "passing in data to a compiled function that expects data - works with helpers in partials")
  public void passingindatatoacompiledfunctionthatexpectsdataworkswithhelpersinpartials()
      throws IOException {
    String result =
        compile("{{>myPartial}}", $("hello", HELLO_NOUN_DATA), $("myPartial", "{{hello}}"))
            .apply(newContext($("noun", "cat"), data("adjective", "happy")));
    assertEquals("happy cat", result);
  }

  @Test
  @DisplayName(
      "passing in data to a compiled function that expects data - works with helpers and"
          + " parameters")
  public void passingindatatoacompiledfunctionthatexpectsdataworkswithhelpersandparameters()
      throws IOException {
    Helper<Object> hello =
        (noun, options) -> {
          Boolean exclaim = options.get("exclaim");
          return options.data("adjective") + " " + noun + (Boolean.TRUE.equals(exclaim) ? "!" : "");
        };
    String result =
        render(
            "{{hello world}}",
            $("hello", hello),
            newContext($("exclaim", true, "world", "world"), data("adjective", "happy")));
    assertEquals("happy world!", result);
  }

  @Test
  @DisplayName(
      "passing in data to a compiled function that expects data - works with block helpers")
  public void passingindatatoacompiledfunctionthatexpectsdataworkswithblockhelpers()
      throws IOException {
    Helper<Object> hello = (context, options) -> options.fn();
    Helper<Object> world =
        (context, options) -> {
          Boolean exclaim = options.get("exclaim");
          return options.data("adjective") + " world" + (Boolean.TRUE.equals(exclaim) ? "!" : "");
        };
    String result =
        render(
            "{{#hello}}{{world}}{{/hello}}",
            $("hello", hello, "world", world),
            newContext($("exclaim", true), data("adjective", "happy")));
    assertEquals("happy world!", result);
  }

  @Test
  @DisplayName(
      "passing in data to a compiled function that expects data - works with block helpers that"
          + " use ..")
  public void passingindatatoacompiledfunctionthatexpectsdataworkswithblockhelpersthatuse()
      throws IOException {
    Helper<Object> hello = (context, options) -> options.fn($("exclaim", "?"));
    Helper<Object> world =
        (thing, options) -> options.data("adjective") + " " + thing + options.get("exclaim", "");
    String result =
        render(
            "{{#hello}}{{world ../zomg}}{{/hello}}",
            $("hello", hello, "world", world),
            newContext($("exclaim", true, "zomg", "world"), data("adjective", "happy")));
    assertEquals("happy world?", result);
  }

  @Test
  @DisplayName(
      "passing in data to a compiled function that expects data - data is passed to with block"
          + " helpers where children use ..")
  public void
      passingindatatoacompiledfunctionthatexpectsdatadataispassedtowithblockhelperswherechildrenuse()
          throws IOException {
    Helper<Object> hello = (context, options) -> options.fn($("exclaim", "?"));
    Helper<Object> world =
        (thing, options) -> options.data("adjective") + " " + thing + options.get("exclaim", "");
    String result =
        render(
            "{{#hello}}{{world ../zomg}}{{/hello}}",
            $("hello", hello, "world", world),
            newContext($("exclaim", true, "zomg", "world"), data("adjective", "happy")));
    assertEquals("happy world?", result);
  }

  @Test
  @DisplayName(
      "passing in data to a compiled function that expects data - data is passed to with block"
          + " helpers where children use ..")
  public void
      passingindatatoacompiledfunctionthatexpectsdatadataispassedtowithblockhelperswherechildrenuse2()
          throws IOException {
    Helper<Object> hello =
        (context, options) -> options.data("accessData") + " " + options.fn($("exclaim", "?"));
    Helper<Object> world =
        (thing, options) -> options.data("adjective") + " " + thing + options.get("exclaim", "");
    String result =
        render(
            "{{#hello}}{{world ../zomg}}{{/hello}}",
            $("hello", hello, "world", world),
            newContext(
                $("exclaim", true, "zomg", "world"),
                data("adjective", "happy", "accessData", "#win")));
    assertEquals("#win happy world?", result);
  }

  @Test
  @DisplayName("you can override inherited data when invoking a helper")
  public void youcanoverrideinheriteddatawheninvokingahelper() throws IOException {
    Helper<Object> hello =
        (context, options) ->
            options.fn(
                Context.newBuilder(options.context, $("exclaim", "?", "zomg", "world"))
                    .build()
                    .data("adjective", "sad"));
    Helper<Object> world =
        (thing, options) -> options.data("adjective") + " " + thing + options.get("exclaim", "");
    String result =
        render(
            "{{#hello}}{{world zomg}}{{/hello}}",
            $("hello", hello, "world", world),
            newContext($("exclaim", true, "zomg", "planet"), data("adjective", "happy")));
    assertEquals("sad world?", result);
  }

  @Test
  @DisplayName("you can override inherited data when invoking a helper with depth")
  public void youcanoverrideinheriteddatawheninvokingahelperwithdepth() throws IOException {
    Helper<Object> hello =
        (context, options) ->
            options.fn(
                Context.newBuilder(options.context, $("exclaim", "?"))
                    .build()
                    .data("adjective", "sad"));
    Helper<Object> world =
        (thing, options) -> options.data("adjective") + " " + thing + options.get("exclaim", "");
    String result =
        render(
            "{{#hello}}{{world ../zomg}}{{/hello}}",
            $("hello", hello, "world", world),
            newContext($("exclaim", true, "zomg", "world"), data("adjective", "happy")));
    assertEquals("sad world?", result);
  }

  @Test
  @DisplayName("the root context can be looked up via @root")
  public void therootcontextcanbelookedupviaroot() throws IOException {
    shouldCompileTo("{{@root.foo}}", $("foo", "hello"), "hello");

    assertEquals("hello", render("{{@root.foo}}", $(), newContext($("foo", "hello"), data())));
  }

  @Test
  @DisplayName("passed root values take priority")
  public void passedrootvaluestakepriority() throws IOException {
    String result =
        render(
            "{{@root.foo}}",
            $(),
            newContext($("foo", "should not be used"), data("root", $("foo", "hello"))));
    assertEquals("hello", result);
  }

  @Test
  @DisplayName("nesting the root context can be looked up via @root")
  public void nestingtherootcontextcanbelookedupviaroot() throws IOException {
    Helper<Object> helper =
        (context, options) -> {
          int depth = options.data("depth");
          return options.fn(
              Context.newBuilder(options.context, context).build().data("depth", depth + 1));
        };
    String result =
        render(
            "{{#helper}}{{#helper}}{{@./depth}} {{@../depth}}"
                + " {{@../../depth}}{{/helper}}{{/helper}}",
            $("helper", helper),
            newContext($("foo", "hello"), data("depth", 0)));
    assertEquals("2 1 0", result);
  }
}
