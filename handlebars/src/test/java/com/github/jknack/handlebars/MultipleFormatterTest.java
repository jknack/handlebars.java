package com.github.jknack.handlebars;

import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class MultipleFormatterTest extends AbstractTest {

  static final long now = System.currentTimeMillis();

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.with(new Formatter() {

      @Override
      public Object format(final Object value, final Chain chain) {
        if (value instanceof Date) {
          return ((Date) value).getTime();
        }
        return chain.format(value);
      }

    }).with(new Formatter() {
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
