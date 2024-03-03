/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i370;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars.Utils;

public class Issue370 {

  @Test
  public void shouldEscapeSingleQuote() {
    assertEquals("&#x27;", Utils.escapeExpression("'").toString());
  }
}
