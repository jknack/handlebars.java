package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class InfiniteLoopTest {

  @Test(expected= HandlebarsException.class)
  public void self() throws IOException {;
    apply("r");
  }

  @Test(expected= HandlebarsException.class)
  public void level1() throws IOException {
    apply("r1");
  }

  @Test(expected= HandlebarsException.class)
  public void level2() throws IOException {
    apply("r2");
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
