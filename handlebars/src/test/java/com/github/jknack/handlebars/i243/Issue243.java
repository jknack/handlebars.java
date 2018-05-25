package com.github.jknack.handlebars.i243;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class Issue243 extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    try {
      return super.newHandlebars()
          .registerHelpers("helpers.js",
              "Handlebars.registerHelper('getIndex', function(index) {\n" +
                  "return index;\n" +
                  "});\n" +
                  "Handlebars.registerHelper('nullHelper', function(context) {\n" +
                  "return context === null ? 'NULL': 'NOT_NULL';\n" +
                  "});");
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  public void zeroValueForJavaScriptHelper() throws IOException {
    shouldCompileTo("{{#each item}}{{getIndex @index}} {{/each}}",
        $("item", new Object[]{10, 20, 30 }), "0.0 1.0 2.0 ");
  }

  @Test
  public void nullValueForJavaScriptHelper() throws IOException {
    shouldCompileTo("{{nullHelper item}}", $("item", null), "NULL");

    shouldCompileTo("{{nullHelper item}}", $("item", new Object()), "NOT_NULL");
  }
}
