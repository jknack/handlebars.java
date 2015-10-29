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
    String expected;
    // cater for difference in rhino and nashorn, it seems nashorn is more clever in not
    // returning pesky doubles when there are int values
    String javaVersion = System.getProperty("java.version");
    if (javaVersion.startsWith("1.8.")) {
      expected = "0 1 2 ";
    } else {
      expected = "0.0 1.0 2.0 ";
    }
    shouldCompileTo("{{#each item}}{{getIndex @index}} {{/each}}",
        $("item", new Object[]{10, 20, 30 }), expected);
  }

  @Test
  public void nullValueForJavaScriptHelper() throws IOException {
    shouldCompileTo("{{nullHelper item}}", $("item", null), "NULL");

    shouldCompileTo("{{nullHelper item}}", $("item", new Object()), "NOT_NULL");
  }
}
