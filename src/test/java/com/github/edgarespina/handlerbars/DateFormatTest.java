package com.github.edgarespina.handlerbars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

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
    String expected = "Tuesday, June 19, 2012";
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"full\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void longFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = "June 19, 2012";
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"long\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void mediumFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = "Jun 19, 2012";
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"medium\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void shortFormat() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = "6/19/12";
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"short\"}}");
    assertEquals(expected, template.apply(date));
  }

  @Test
  public void pattern() throws IOException {
    Date date = date(19, 6, 2012);
    String expected = "19/06/2012";
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{dateFormat this \"dd/MM/yyyy\"}}");
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
