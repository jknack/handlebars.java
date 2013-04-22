package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class SafeStringTest {

  @Test
  public void equals() {
    assertEquals(new Handlebars.SafeString("hello"), new Handlebars.SafeString(
        "hello"));
  }

  @Test
  public void notEquals() {
    assertNotSame(new Handlebars.SafeString("hello"),
        new Handlebars.SafeString(
            "hello!"));
  }

  @Test
  public void hashcode() {
    assertEquals(new Handlebars.SafeString("hello").hashCode(),
        new Handlebars.SafeString(
            "hello").hashCode());
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
