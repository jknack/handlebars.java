/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue465 extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.with(EscapingStrategy.HBS3);
  }

  @Test
  public void withHbs3EscapingStrategy() throws IOException {
    shouldCompileTo("equals is {{eq}}", $("hash", $("eq", "=")), "equals is =");
  }
}
