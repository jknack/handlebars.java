/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;

/**
 * Tests for <code>JodaHelper</code>.
 *
 * @author @mrhanlon https://github.com/mrhanlon
 */
public class JodaHelperTest extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    Handlebars handlebars = super.newHandlebars();
    handlebars.registerHelper("jodaPatternHelper", JodaHelper.jodaPattern);
    handlebars.registerHelper("jodaStyleHelper", JodaHelper.jodaStyle);
    handlebars.registerHelper("jodaISOHelper", JodaHelper.jodaISO);
    return handlebars;
  }

  @Test
  public void testPattern() throws IOException {
    DateTime dateTime = new DateTime().withDate(1995, 7, 4).withTime(14, 32, 12, 0);
    shouldCompileTo(
        "{{jodaPatternHelper this \"y-MMM-d H:m:s\"}}", dateTime, "1995-Jul-4 14:32:12");
  }

  @Test
  public void testBadPattern() throws IOException {
    DateTime dateTime = new DateTime().withDate(1995, 7, 4).withTime(14, 32, 12, 0);
    try {
      shouldCompileTo("{{jodaPatternHelper this \"qwerty\"}}", dateTime, "1995-Jul-4 14:32:12");
      fail("Exception should have thrown!");
    } catch (HandlebarsException e) {
      Throwable t = e.getCause();
      assertEquals("Illegal pattern component: q", t.getMessage());
    }
  }

  @Test
  public void testStyle() throws IOException {
    DateTime dateTime = new DateTime().withDate(1995, 7, 4).withTime(14, 32, 12, 0);
    withJava(
        version -> version <= 8,
        () -> shouldCompileTo("{{jodaStyleHelper this \"SS\"}}", dateTime, "7/4/95 2:32 PM"));
    withJava(
        version -> version >= 9,
        () -> shouldCompileTo("{{jodaStyleHelper this \"SS\"}}", dateTime, "7/4/95, 2:32 PM"));
  }

  @Test
  public void testBadStyle() throws IOException {
    DateTime dateTime = new DateTime().withDate(1995, 7, 4).withTime(14, 32, 12, 0);
    try {
      shouldCompileTo("{{jodaStyleHelper this \"QS\"}}", dateTime, "");
    } catch (HandlebarsException e) {
      Throwable t = e.getCause();
      assertEquals("Invalid style character: Q", t.getMessage());
    }
  }

  @Test
  public void testISO() throws IOException {
    DateTime dateTime =
        new DateTime()
            .withDate(1995, 7, 4)
            .withTime(14, 32, 12, 0)
            .withZoneRetainFields(DateTimeZone.UTC);
    shouldCompileTo("{{jodaISOHelper this}}", dateTime, "1995-07-04T14:32:12Z");
  }
}
