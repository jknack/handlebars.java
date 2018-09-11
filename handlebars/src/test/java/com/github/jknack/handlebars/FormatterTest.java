package com.github.jknack.handlebars;

import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class FormatterTest extends AbstractTest {

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
