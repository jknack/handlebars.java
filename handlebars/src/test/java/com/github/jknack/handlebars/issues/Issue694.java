/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue694 extends AbstractTest {

  @Test
  public void shouldRenderPartialBlockInsideInlinePartial() throws IOException {
    shouldCompileToWithPartials(
        "{{#*inline \"hello\"}}Hello{{/inline}}"
            + "{{#*inline \"render\"}}{{> @partial-block}}{{/inline}}"
            + "{{#> render}}"
            + "{{#*inline \"inlineTemplate\"}}{{#> hello}}{{/hello}}{{/inline}}"
            + "{{> inlineTemplate}}"
            + "{{/render}}",
        $,
        $(),
        "Hello");
  }
}
