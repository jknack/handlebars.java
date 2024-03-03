/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ScalarContextTest {

  private String selector;

  public ScalarContextTest(final String selector) {
    this.selector = selector;
  }

  @Test
  public void integer() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var i = {{" + selector + "}};");
    assertEquals("var i = 10;", template.apply(10));
  }

  @Test
  public void string() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = 'Hello';", template.apply("Hello"));
  }

  @Test
  public void quoteParam() throws IOException {
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

  @Test
  public void quoteHash() throws IOException {
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

  @Test
  public void array() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compileInline("{{#" + selector + "}}{{" + selector + "}} {{/" + selector + "}}");
    assertEquals("1 2 3 ", template.apply(new Object[] {1, 2, 3}));
  }

  @Test
  public void list() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compileInline("{{#" + selector + "}}{{" + selector + "}} {{/" + selector + "}}");
    assertEquals("1 2 3 ", template.apply(new Object[] {1, 2, 3}));
  }

  @Test
  public void dontEscape() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{{" + selector + "}}}';");
    assertEquals("var s = '<div>';", template.apply("<div>"));
  }

  @Test
  public void safeString() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = '<div>';", template.apply(new Handlebars.SafeString("<div>")));
  }

  @Test
  public void ch() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = 'c';", template.apply('c'));
  }

  @Test
  public void chHtml() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = '&lt;';", template.apply('<'));
  }

  @Test
  public void htmlString() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var s = '{{" + selector + "}}';");
    assertEquals("var s = '&lt;div&gt;';", template.apply("<div>"));
  }

  @Test
  public void bool() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("if ({{" + selector + "}})");
    assertEquals("if (true)", template.apply(true));
  }

  @Test
  public void decimal() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("var d = {{" + selector + "}};");
    assertEquals("var d = 1.34;", template.apply(1.34));
  }

  @Parameters
  public static Collection<Object[]> data() {
    Collection<Object[]> selectors = new ArrayList<>();
    selectors.add(new Object[] {"."});
    selectors.add(new Object[] {"this"});
    return selectors;
  }
}
