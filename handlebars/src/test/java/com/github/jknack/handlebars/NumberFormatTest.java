/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Test;

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
    String expected =
        new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.getDefault())).format(number);
    shouldCompileTo("{{numberFormat this \"" + pattern + "\"}}", number, expected);
  }

  @Test
  public void frLocale() throws IOException {
    Number number = Math.PI;
    String pattern = "#,###,##0.000";
    String expected =
        new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.FRENCH)).format(number);
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
