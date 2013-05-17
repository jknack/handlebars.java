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

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.github.jknack.handlebars.helper.StringHelpers;

public class NumberFormatTest extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper(StringHelpers.numberFormat.name(), StringHelpers.numberFormat);
    return handlebars;
  }

  @Test
  public void defaultFormat() throws IOException {
    Number number = Math.PI;
    String expected = NumberFormat.getNumberInstance(Locale.getDefault()).format(number);
    shouldCompileTo("{{numberFormat this}}", number, expected);
  }

  @Test
  public void currencyFormat() throws IOException {
    Number number = Math.PI;
    String expected = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(number);
    shouldCompileTo("{{numberFormat this \"currency\"}}", number, expected);
  }

  @Test
  public void percentFormat() throws IOException {
    Number number = Math.PI;
    String expected = NumberFormat.getPercentInstance(Locale.getDefault()).format(number);
    shouldCompileTo("{{numberFormat this \"percent\"}}", number, expected);
  }

  @Test
  public void integerFormat() throws IOException {
    Number number = Math.PI;
    String expected = NumberFormat.getIntegerInstance(Locale.getDefault()).format(number);
    shouldCompileTo("{{numberFormat this \"integer\"}}", number, expected);
  }

  @Test
  public void pattern() throws IOException {
    Number number = Math.PI;
    String pattern = "#,###,##0.000";
    String expected = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.getDefault()))
        .format(number);
    shouldCompileTo("{{numberFormat this \"" + pattern + "\"}}", number, expected);
  }

  @Test
  public void frLocale() throws IOException {
    Number number = Math.PI;
    String pattern = "#,###,##0.000";
    String expected = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.FRENCH))
        .format(number);
    shouldCompileTo("{{numberFormat this \"" + pattern + "\" \"fr\"}}", number, expected);
  }

  public static Date date(final int day, final int month, final int year) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DATE, day);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.YEAR, year);
    return calendar.getTime();
  }
}
