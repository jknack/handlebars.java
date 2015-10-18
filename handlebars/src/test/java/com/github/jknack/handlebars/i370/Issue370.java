package com.github.jknack.handlebars.i370;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars.Utils;

public class Issue370 {

  @Test
  public void shouldEscapeSingleQuote() {
    assertEquals("&#x27;", Utils.escapeExpression("'").toString());
  }
}
