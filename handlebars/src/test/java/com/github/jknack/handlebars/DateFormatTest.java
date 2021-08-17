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
package com.github.jknack.handlebars;

import com.github.jknack.handlebars.helper.StringHelpers;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatTest extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper(StringHelpers.dateFormat.name(), StringHelpers.dateFormat);
    return handlebars;
  }

  @Test
  public void defaultFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
            .format(date);
    shouldCompileTo("{{dateFormat this}}", date, expected);
  }

  @Test
  public void fullFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault())
            .format(date);
    shouldCompileTo("{{dateFormat this \"full\"}}", date, expected);
  }

  @Test
  public void longFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
            .format(date);
    shouldCompileTo("{{dateFormat this \"long\"}}", date, expected);
  }

  @Test
  public void mediumFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
            .format(date);
    shouldCompileTo("{{dateFormat this \"medium\"}}", date, expected);
  }

  @Test
  public void shortFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
            .format(date);
    shouldCompileTo("{{dateFormat this \"short\"}}", date, expected);
  }

  @Test
  public void pattern() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    shouldCompileTo("{{dateFormat this \"dd/MM/yyyy\"}}", date, expected);
  }

  @Test
  public void frLocale() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH)
            .format(date);
    shouldCompileTo("{{dateFormat this \"short\" \"fr\"}}", date, expected);
  }

  public static Date date(final int day, final int month, final int year) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DATE, day);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.YEAR, year);
    return calendar.getTime();
  }

  public void verifyDateTimeFormats(Object date) throws IOException {
    shouldCompileTo("{{dateFormat this \"medium\" locale=\"de\"}}", date, "12.08.2021");
    shouldCompileTo("{{dateFormat this \"medium\" \"en_EN\"}}", date, "Aug 12, 2021");
    shouldCompileTo("{{dateFormat this \"dd\"}}", date, "12");
    shouldCompileTo("{{dateFormat this \"'week' w '@' mm:ss\" \"de_DE\"}}", date, "week 32 @ 38:55");

    TemporalAccessor temporalAccessor;
    if (date instanceof Date) {
      temporalAccessor = ((Date) date).toInstant();
    } else {
      temporalAccessor = (TemporalAccessor) date;
    }

    // "12.08.2021 16:38:55" with jdk 8, but "12.08.2021, 16:38:55" with jdk 11
    String expected1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN).withZone(ZoneId.of("Europe/Berlin")).format(temporalAccessor);
    shouldCompileTo("{{dateFormat this time=\"medium\" locale=\"de\" tz=\"Europe/Berlin\"}}", date, expected1);

    // "12. August 2021 06:38" with jdk 8, but "12. August 2021, 06:38" with jdk 11
    String expected2 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
        .withLocale(Locale.GERMAN).withZone(ZoneId.of("GMT-8")).format(temporalAccessor);
    shouldCompileTo("{{dateFormat this \"long\" locale=\"de\" time=\"short\" tz=\"GMT-8:00\"}}", date, expected2);

    String expected3 = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()).format(temporalAccessor);
    shouldCompileTo("{{dateFormat this}}", date, expected3);

    String expected4 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()).format(temporalAccessor);
    shouldCompileTo("{{dateFormat this time=\"medium\"}}", date, expected4);
  }

  @Test
  public void formatDate() throws IOException {
    verifyDateTimeFormats(new Date(1628779135*1000L));
  }

  @Test
  public void formatInstant() throws IOException {
    verifyDateTimeFormats(Instant.ofEpochSecond(1628779135));
  }

  @Test
  public void formatOffsetDateTime() throws IOException {
    verifyDateTimeFormats(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1628779135), ZoneOffset.UTC));
  }

  @Test
  public void formatLocalDateTime() throws IOException {
    verifyDateTimeFormats(LocalDateTime.ofInstant(Instant.ofEpochSecond(1628779135), ZoneOffset.UTC));
  }
}
