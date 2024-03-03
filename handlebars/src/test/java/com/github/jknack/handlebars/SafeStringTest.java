/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

public class SafeStringTest {

  @Test
  public void equals() {
    assertEquals(new Handlebars.SafeString("hello"), new Handlebars.SafeString("hello"));
  }

  @Test
  public void notEquals() {
    assertNotSame(new Handlebars.SafeString("hello"), new Handlebars.SafeString("hello!"));
  }

  @Test
  public void hashcode() {
    assertEquals(
        new Handlebars.SafeString("hello").hashCode(),
        new Handlebars.SafeString("hello").hashCode());
  }

  @Test
  public void length() {
    assertEquals(5, new Handlebars.SafeString("hello").length());
  }

  @Test
  public void charAt() {
    assertEquals('e', new Handlebars.SafeString("hello").charAt(1));
  }

  @Test
  public void substring() {
    assertEquals("el", new Handlebars.SafeString("hello").subSequence(1, 3));
  }
}
