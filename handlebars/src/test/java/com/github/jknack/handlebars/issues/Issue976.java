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

public class Issue976 extends AbstractTest {

  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.setStartDelimiter("${");
    handlebars.setEndDelimiter("}$");
  }

  @Test
  public void shouldNotThrowSyntaxError() throws IOException {
    shouldCompileTo("${var1}$. ${var2}$", $("var1", "a", "var2", "b"), "a. b");
  }
}
