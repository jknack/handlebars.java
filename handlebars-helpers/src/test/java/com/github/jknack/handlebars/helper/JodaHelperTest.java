/**
 * Copyright (c) 2012-2013 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.helper;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;

/**
 * Tests for <code>JodaHelper</code>.
 *
 * @author @mrhanlon https://github.com/mrhanlon
 *
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
    shouldCompileTo("{{jodaPatternHelper this \"y-MMM-d H:m:s\"}}", dateTime, "1995-Jul-4 14:32:12");
  }

  @Test
  public void testBadPattern() throws IOException {
    DateTime dateTime = new DateTime().withDate(1995, 7, 4).withTime(14, 32, 12, 0);
    try {
      shouldCompileTo("{{jodaPatternHelper this \"qwerty\"}}", dateTime, "1995-Jul-4 14:32:12");
      Assert.fail("Exception should have thrown!");
    } catch (HandlebarsException e) {
      Throwable t = e.getCause();
      Assert.assertEquals("Illegal pattern component: q", t.getMessage());
    }
  }

  @Test
  public void testStyle() throws IOException {
    DateTime dateTime = new DateTime().withDate(1995, 7, 4).withTime(14, 32, 12, 0);
    shouldCompileTo("{{jodaStyleHelper this \"SS\"}}", dateTime, "7/4/95, 2:32 PM");
  }

  @Test
  public void testBadStyle() throws IOException {
    DateTime dateTime = new DateTime().withDate(1995, 7, 4).withTime(14, 32, 12, 0);
    try {
      shouldCompileTo("{{jodaStyleHelper this \"QS\"}}", dateTime, "");
    } catch (HandlebarsException e) {
      Throwable t = e.getCause();
      Assert.assertEquals("Invalid style character: Q", t.getMessage());
    }
  }

  @Test
  public void testISO() throws IOException {
    DateTime dateTime = new DateTime().withDate(1995, 7, 4).withTime(14, 32, 12, 0).withZoneRetainFields(DateTimeZone.UTC);
    shouldCompileTo("{{jodaISOHelper this}}", dateTime, "1995-07-04T14:32:12Z");
  }
}
