/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.StringHelpers;

public class Issue701 extends AbstractTest {

  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.registerHelpers(StringHelpers.class);
  }

  @Test
  public void expressionCanStartWithNumber() throws IOException {
    shouldCompileTo("{{replace content '\n' ' '}}", $("content", "A\nB\nC"), "A B C");
  }
}
