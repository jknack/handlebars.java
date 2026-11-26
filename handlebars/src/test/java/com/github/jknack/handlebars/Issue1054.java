/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue1054 extends AbstractTest {

  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.startDelimiter("{").endDelimiter("}");
  }

  @Test
  public void shouldEscapeSingleCharExpression() throws IOException {
    shouldCompileTo("\\{text}", $, "{text}");
    shouldCompileTo("x\\{text}y", $, "x{text}y");
  }
}
