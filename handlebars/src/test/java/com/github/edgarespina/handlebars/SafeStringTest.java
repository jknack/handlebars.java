package com.github.edgarespina.handlebars;

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
}
