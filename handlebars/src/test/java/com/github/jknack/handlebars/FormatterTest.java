/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

public class FormatterTest extends AbstractTest {

  static final long now = System.currentTimeMillis();

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.with(
        new Formatter() {
          @Override
          public Object format(final Object value, final Chain chain) {
            if (value instanceof Date) {
              return ((Date) value).getTime();
            }
            return chain.format(value);
          }
        });
  }

  @Test
  public void useFormatterTwice() throws IOException {
    Template t = compile("time is {{this}}/{{this}}");

    assertEquals("time is " + now + "/" + now, t.apply(new Date(now)));
  }

  @Test
  public void testFormatterWithoutMatch() throws IOException {
    Template t = compile("string is {{this}}");

    assertEquals("string is testvalue", t.apply("testvalue"));
  }

  @Test
  public void useTemplateTwice() throws IOException {
    Template t = compile("time is {{this}}");

    assertEquals("time is " + now, t.apply(new Date(now)));
    assertEquals("time is " + now, t.apply(new Date(now)));
  }
}
