package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

public class Issue614 extends v4Test {

  @Override protected void configure(Handlebars handlebars) {
    try {
      handlebars.registerHelpers("helpers.js",
          "Handlebars.registerHelper('number', function(value) {\n"
              + "   return value;\n"
              + "});\n"
              + "\n"
              + "Handlebars.registerHelper('len', function(array) {\n"
              + "   return array.length;\n"
              + "});");
    } catch (Exception x) {
      throw new IllegalStateException(x);
    }
  }

  @Test
  public void shouldFavorIntOverDouble() throws Exception {
    shouldCompileTo("{{number this}}", $("hash", 3), "3");
    shouldCompileTo("{{number this}}", $("hash", 3.1), "3.1");

    shouldCompileTo("{{len this}}", $("hash", new Object[]{1, 2, 3, 4}), "4");
    shouldCompileTo("{{len this}}", $("hash", Arrays.asList(1, 2, 3, 4)), "4");
  }
}
