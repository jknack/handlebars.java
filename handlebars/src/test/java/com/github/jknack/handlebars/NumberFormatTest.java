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

  @Test
  public void namedFormat() throws IOException {
    final Number number = Math.PI;
    final Locale defaultLocale = Locale.getDefault();
    final String expected = NumberFormat
            .getPercentInstance(defaultLocale)
            .format(number);

    shouldCompileTo("{{numberFormat this format=\"percent\"}}", number, expected);
  }

  @Test
  public void namedBrLocale() throws IOException {
    final Number number = Math.PI;
    final String pattern = "currency";
    final Locale portuguese = Locale.forLanguageTag("pt-BR");

    final String expected = NumberFormat
            .getCurrencyInstance(portuguese)
            .format(number);

    shouldCompileTo("{{numberFormat this \"" + pattern + "\" locale=\"pt-BR\"}}", number, expected);
  }
}
