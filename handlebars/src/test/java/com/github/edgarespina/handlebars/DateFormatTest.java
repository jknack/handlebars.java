/**
 * Copyright (c) 2012 Edgar Espina
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
package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

public class DateFormatTest {

  private static Handlebars handlebars = new Handlebars();

  @BeforeClass
  public static void setup() {
    StringHelpers.register(handlebars);
  }

  @Test
  public void defaultFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
            .format(date);
    Template template = handlebars.compile("{{dateFormat this}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void fullFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault())
            .format(date);
    Template template = handlebars.compile("{{dateFormat this \"full\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void longFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
            .format(date);
    Template template = handlebars.compile("{{dateFormat this \"long\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void mediumFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
            .format(date);
    Template template = handlebars.compile("{{dateFormat this \"medium\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void shortFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
            .format(date);
    Template template = handlebars.compile("{{dateFormat this \"short\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void pattern() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    Template template =
        handlebars.compile("{{dateFormat this \"dd/MM/yyyy\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void frLocale() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH)
            .format(date);
    Template template =
        handlebars.compile("{{dateFormat this \"short\" \"fr\"}}");
    assertEquals(expected, template.apply(date));
  }

  public static Date date(final int day, final int month, final int year) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DATE, day);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.YEAR, year);
    return calendar.getTime();
  }
}
