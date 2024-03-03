/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ScalarContextTest {

  @ParameterizedTest
  @MethodSource("data")
  public void integer(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var i = {{" + selector + "}};");
    assertEquals("var i = 10;", template.apply(10));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void string(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = 'Hello';", template.apply("Hello"));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void quoteParam(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper(
        "quote",
        new Helper<String>() {
          @Override
          public Object apply(final String context, final Options options) throws IOException {
            return context;
          }
        });
    Template template = handlebars.compileInline("{{{quote \"2\\\"secs\"}}}");
    assertEquals("2\"secs", template.apply(new Object()));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void quoteHash(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper(
        "quote",
        new Helper<String>() {
          @Override
          public Object apply(final String context, final Options options) throws IOException {
            return (CharSequence) options.hash.get("q");
          }
        });
    Template template = handlebars.compileInline("{{{quote q=\"2\\\"secs\"}}}");
    assertEquals("2\"secs", template.apply(null));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void array(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compileInline("{{#" + selector + "}}{{" + selector + "}} {{/" + selector + "}}");
    assertEquals("1 2 3 ", template.apply(new Object[] {1, 2, 3}));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void list(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compileInline("{{#" + selector + "}}{{" + selector + "}} {{/" + selector + "}}");
    assertEquals("1 2 3 ", template.apply(new Object[] {1, 2, 3}));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void dontEscape(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{{" + selector + "}}}';");
    assertEquals("var s = '<div>';", template.apply("<div>"));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void safeString(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = '<div>';", template.apply(new Handlebars.SafeString("<div>")));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void ch(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = 'c';", template.apply('c'));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void chHtml(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = '&lt;';", template.apply('<'));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void htmlString(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = '&lt;div&gt;';", template.apply("<div>"));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void bool(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("if ({{" + selector + "}})");
    assertEquals("if (true)", template.apply(true));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void decimal(String selector) throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var d = {{" + selector + "}};");
    assertEquals("var d = 1.34;", template.apply(1.34));
  }

  public static Stream<String> data() {
    return Stream.of(".", "this");
  }
}
