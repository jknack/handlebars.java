package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class ScopeTest extends AbstractTest {

  @Test
  public void nameConflict() throws IOException {
    Object context = $("name", "an attribute", "inner", $("name", "an inner attribute"));

    // simple
    shouldCompileTo("{{name}}", context, "an attribute");

    // qualified
    shouldCompileTo("{{this.name}}", context, "an attribute");

    // simple inner
    shouldCompileTo("{{#inner}}{{name}}{{/inner}}", context, "an inner attribute");

    // qualified inner
    shouldCompileTo("{{#inner}}{{this.name}}{{/inner}}", context, "an inner attribute");

    // path inner
    shouldCompileTo("{{inner.name}}", context, "an inner attribute");

    // A name conflict
    Hash helpers = $("name", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return "helper response";
      }
    });

    // helper
    shouldCompileTo("{{name}}", context, helpers, "helper response");

    // attribute
    shouldCompileTo("{{this.name}}", context, helpers, "an attribute");

    // simple inner helper
    shouldCompileTo("{{#inner}}{{name}}{{/inner}}", context, helpers, "helper response");

    // qualified inner attribute
    shouldCompileTo("{{#inner}}{{this.name}}{{/inner}}", context, helpers, "an inner attribute");
    shouldCompileTo("{{inner.name}}", context, helpers, "an inner attribute");
  }

  @Test
  public void currentScope() throws IOException {
    Object context = $("value", "parent", "child", $);

    // Don't expand the scope resolution over the context stack
    shouldCompileTo("{{#child}}{{this.value}}{{/child}}", context, "");

    // Expand the scope resolution over the context stack
    shouldCompileTo("{{#child}}{{value}}{{/child}}", context, "parent");
  }
}
