package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue468 extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers("helpers.js", "\n"
          + "Handlebars.registerHelper('raw-helper', function(options) {\n"
          + "  return options.fn();\n"
          + "});");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Test
  public void rawHelperShouldWork() throws IOException {
    shouldCompileTo("{{{{raw-helper}}}}{{bar}}{{{{/raw-helper}}}}", $, "{{bar}}");
  }

}
