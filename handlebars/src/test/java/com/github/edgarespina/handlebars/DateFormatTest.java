package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.github.edgarespina.handlebars.Handlebars;
import com.github.edgarespina.handlebars.Template;

public class DateFormatTest {

  @Test
  public void defaultFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = DateFormat.getDateInstance().format(date);
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void fullFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = DateFormat.getDateInstance(DateFormat.FULL).format(date);
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"full\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void longFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = DateFormat.getDateInstance(DateFormat.LONG).format(date);
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"long\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void mediumFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected =
        DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"medium\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void shortFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"short\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void pattern() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = new SimpleDateFormat("dd/MM/yyyy").format(date);
    Handlebars handlebars = new Handlebars();
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
    Handlebars handlebars = new Handlebars();
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
