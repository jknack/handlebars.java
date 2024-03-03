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

public class MultipleFormatterTest extends AbstractTest {

  static final long now = System.currentTimeMillis();

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars
        .with(
            new Formatter() {

              @Override
              public Object format(final Object value, final Chain chain) {
                if (value instanceof Date) {
                  return ((Date) value).getTime();
                }
                return chain.format(value);
              }
            })
        .with(
            new Formatter() {
              @Override
              public Object format(final Object value, final Chain chain) {

                if (value instanceof Integer) {
                  return Integer.toHexString((Integer) value);
                }
                return chain.format(value);
              }
            });
  }

  @Test
  public void testDateFormatter() throws IOException {
    Template t = compile("time is {{this}}");

    assertEquals("time is " + now, t.apply(new Date(now)));
  }

  @Test
  public void testIntegerFormatter() throws IOException {
    Template t = compile("Hex-Value is {{this}}");

    assertEquals("Hex-Value is 10", t.apply(16));
  }
}
