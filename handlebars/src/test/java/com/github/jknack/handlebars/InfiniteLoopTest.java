/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class InfiniteLoopTest {

  @Test
  public void self() throws IOException {
    assertThrows(HandlebarsException.class, () -> apply("r"));
  }

  @Test
  public void level1() throws IOException {
    assertThrows(HandlebarsException.class, () -> apply("r1"));
  }

  @Test
  public void level2() throws IOException {
    assertThrows(HandlebarsException.class, () -> apply("r2"));
  }

  private void apply(final String path) throws IOException {
    try {
      new Handlebars().compile(path).apply(new Object());
    } catch (HandlebarsException ex) {
      Handlebars.log(ex.getMessage());
      throw ex;
    }
  }
}
