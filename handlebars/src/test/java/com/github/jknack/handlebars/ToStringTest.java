/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class ToStringTest {

  public static class UnsafeString {
    String underlying;

    public UnsafeString(final String underlying) {
      this.underlying = underlying;
    }

    @Override
    public String toString() {
      return "<h1>" + underlying + "</h1>";
    }
  }

  @Test
  public void unsafeString() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{this}}");

    String result = template.apply(new UnsafeString("Hello"));

    assertEquals("&lt;h1&gt;Hello&lt;/h1&gt;", result);
  }
}
