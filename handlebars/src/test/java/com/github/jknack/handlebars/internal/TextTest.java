/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;

/**
 * Unit test for {@link Text}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class TextTest {

  @Test
  public void newText() {
    assertEquals("a", new Text(new Handlebars(), "a").text());
  }

  @Test
  public void newTextSequence() {
    assertEquals("abc", new Text(new Handlebars(), "abc").text());
  }

  @Test(expected = NullPointerException.class)
  public void newTextFail() {
    new Text(new Handlebars(), null);
  }
}
