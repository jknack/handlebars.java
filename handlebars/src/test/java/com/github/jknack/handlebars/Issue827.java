/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.helper.ConditionalHelpers;

public class Issue827 extends v4Test {

  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.registerHelpers(ConditionalHelpers.class);
  }

  @Test
  public void shouldIgnoreDifferentNumTypes() throws IOException {
    shouldCompileTo("{{gt 5 2.0}}", $(), "true");

    shouldCompileTo("{{eq 5 2.0}}", $(), "false");

    shouldCompileTo("{{eq 2 2.0}}", $(), "true");
  }
}
