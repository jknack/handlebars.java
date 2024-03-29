/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

public class JavaVersionTest {

  @Test
  public void shouldCheckVersion8() {
    assumeTrue(Handlebars.Utils.javaVersion() == 8);
    assertEquals(8, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion9() {
    assumeTrue(Handlebars.Utils.javaVersion() == 9);
    assertEquals(9, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion10() {
    assumeTrue(Handlebars.Utils.javaVersion() == 10);
    assertEquals(10, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion11() {
    assumeTrue(Handlebars.Utils.javaVersion() == 11);
    assertEquals(11, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion12() {
    assumeTrue(Handlebars.Utils.javaVersion() == 12);
    assertEquals(12, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion13() {
    assumeTrue(Handlebars.Utils.javaVersion() == 13);
    assertEquals(13, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion14() {
    assumeTrue(Handlebars.Utils.javaVersion() == 14);
    assertEquals(14, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion15() {
    assumeTrue(Handlebars.Utils.javaVersion() == 15);
    assertEquals(15, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion16() {
    assumeTrue(Handlebars.Utils.javaVersion() == 16);
    assertEquals(16, Handlebars.Utils.javaVersion());
  }

  @Test
  public void shouldCheckVersion17() {
    assumeTrue(Handlebars.Utils.javaVersion() == 17);
    assertEquals(17, Handlebars.Utils.javaVersion());
  }
}
