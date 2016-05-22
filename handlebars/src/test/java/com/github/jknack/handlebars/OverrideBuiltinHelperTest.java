package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class OverrideBuiltinHelperTest extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelper("each", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return "custom";
      }
    });
  }

  @Test
  public void overrideEach() throws IOException {
    shouldCompileTo("{{#each this}}{{this}}{{/each}}", $("hash", new Object[]{1, 2, 3 }), "custom");
  }
}
