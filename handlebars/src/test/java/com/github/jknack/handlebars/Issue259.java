/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class Issue259 {

  @Test
  public void smallBigDecimal() throws IOException {
    assertFalse(Handlebars.Utils.isEmpty(new BigDecimal("0.01")));
  }

  @Test
  public void zeroBigDecimal() throws IOException {
    assertTrue(Handlebars.Utils.isEmpty(new BigDecimal("0.0000")));
  }
}
