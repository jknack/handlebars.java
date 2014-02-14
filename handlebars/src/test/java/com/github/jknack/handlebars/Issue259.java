package com.github.jknack.handlebars;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Test;

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
