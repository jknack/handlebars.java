/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i407;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class Issue407 extends AbstractTest {
  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers(new File("src/test/resources/issue407.js"));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  public void shouldNotThrowNPE() throws IOException {
    shouldCompileTo(
        "{{#compare true true}}\n"
            + "    {{uppercase \"aaa\"}}\n"
            + "{{else}}\n"
            + "    {{uppercase \"bbb\"}}\n"
            + "{{/compare}}",
        $,
        "\n    AAA\n");

    shouldCompileTo(
        "{{#compare true true}}\n"
            + "    {{#compare true true}}\n"
            + "        kkk\n"
            + "    {{/compare}}\n"
            + "{{/compare}}",
        $,
        "\n" + "    \n" + "        kkk\n" + "    \n");
  }
}
