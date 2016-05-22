package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue483b extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers("483.js",
          "Handlebars.registerHelper('equal', function (arg1, arg2) {\n" +
              "    return (arg1 == arg2);\n" +
              "});");
    } catch (Exception ex) {
      Assert.fail(ex.getMessage());
    }
  }

  @Test
  public void shouldPassObjectResult() throws IOException {
    shouldCompileTo("{{#if (equal arg 'foo')}}foo{{/if}}",
        $("hash", $("arg", "foo")), "foo");

    shouldCompileTo("{{#if (equal arg 'foo')}}foo{{/if}}",
        $("hash", $("arg", "bar")), "");
  }

}
