package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class HumanizeHelperTest {
  private static final Handlebars handlebars = new Handlebars();

  static {
    HumanizeHelper.register(handlebars);
  }

  @Test
  public void binaryPrefix() throws IOException {
    assertEquals("2 bytes",
        handlebars.compile("{{binaryPrefix this}}").apply(2));

    assertEquals("1.5 kB",
        handlebars.compile("{{binaryPrefix this}}").apply(1536));

    assertEquals("5 MB",
        handlebars.compile("{{binaryPrefix this}}").apply(5242880));
  }

  @Test
  public void camelize() throws IOException {
    assertEquals("ThisIsCamelCase",
        handlebars.compile("{{camelize this}}").apply("This is camel case"));

    assertEquals("thisIsCamelCase",
        handlebars.compile("{{camelize this capFirst=false}}")
            .apply("This is camel case"));
  }

  @Test
  public void decamelize() throws IOException {
    assertEquals("this Is Camel Case",
        handlebars.compile("{{decamelize this}}").apply("thisIsCamelCase"));

    assertEquals("This Is Camel Case",
        handlebars.compile("{{decamelize this}}")
            .apply("ThisIsCamelCase"));

    assertEquals("ThisxIsxCamelxCase",
        handlebars.compile("{{decamelize this replacement=\"x\"}}")
            .apply("ThisIsCamelCase"));
  }

  /**
   * Note: beside locale is optional it must be set in unit testing, otherwise
   * the test might fail in a different machine.
   *
   * @throws IOException
   */
  @Test
  public void formatCurrency_es_AR() throws IOException {
    assertEquals("$34",
        handlebars.compile("{{formatCurrency this locale=\"es_AR\"}}")
            .apply(34));

    assertEquals("$1.000",
        handlebars.compile("{{formatCurrency this locale=\"es_AR\"}}")
            .apply(1000));

    assertEquals("$12,50",
        handlebars.compile("{{formatCurrency this locale=\"es_AR\"}}")
            .apply(12.5));
  }

  /**
   * Note: beside locale is optional it must be set in unit testing, otherwise
   * the test might fail in a different machine.
   *
   * @throws IOException
   */
  @Test
  public void formatCurrency_en_GB() throws IOException {
    assertEquals("£34",
        handlebars.compile("{{formatCurrency this locale=\"en_GB\"}}")
            .apply(34));

    assertEquals("£1,000",
        handlebars.compile("{{formatCurrency this locale=\"en_GB\"}}")
            .apply(1000));

    assertEquals("£12.50",
        handlebars.compile("{{formatCurrency this locale=\"en_GB\"}}")
            .apply(12.5));
  }

  @Test
  public void formatPercent() throws IOException {
    assertEquals("50%",
        handlebars.compile("{{formatPercent this}}").apply(0.5));

    assertEquals("100%",
        handlebars.compile("{{formatPercent this}}").apply(1));

    assertEquals("56%",
        handlebars.compile("{{formatPercent this}}").apply(0.564));
  }

  @Test
  public void metricPrefix() throws IOException {
    assertEquals("200",
        handlebars.compile("{{metricPrefix this}}").apply(200));

    assertEquals("1k",
        handlebars.compile("{{metricPrefix this}}").apply(1000));

    assertEquals("3.5M",
        handlebars.compile("{{metricPrefix this}}").apply(3500000));
  }

  @Test
  public void naturalDay() throws IOException {
    Calendar calendar = Calendar.getInstance();

    Date now = calendar.getTime();

    calendar.add(Calendar.HOUR, -24);
    Date yesterday = calendar.getTime();

    calendar.add(Calendar.HOUR, 24 * 2);
    Date tomorrow = calendar.getTime();

    assertEquals(
        "yesterday",
        handlebars.compile("{{naturalDay this locale=\"en_US\"}}").apply(
            yesterday));

    assertEquals("today",
        handlebars.compile("{{naturalDay this locale=\"en_US\"}}").apply(now));

    assertEquals(
        "tomorrow",
        handlebars.compile("{{naturalDay this locale=\"en_US\"}}").apply(
            tomorrow));
  }

  @Test
  public void naturalTime() throws IOException, InterruptedException {
    Calendar calendar = Calendar.getInstance();

    Date now = calendar.getTime();

    Thread.sleep(1000);

    assertEquals("moments ago",
        handlebars.compile("{{naturalTime this locale=\"en_US\"}}")
            .apply(now));

  }

  @Test
  public void ordinal() throws IOException {
    assertEquals("1st",
        handlebars.compile("{{ordinal this locale=\"en_US\"}}")
            .apply(1));

    assertEquals("2nd",
        handlebars.compile("{{ordinal this locale=\"en_US\"}}")
            .apply(2));

    assertEquals("3rd",
        handlebars.compile("{{ordinal this locale=\"en_US\"}}")
            .apply(3));

    assertEquals("10th",
        handlebars.compile("{{ordinal this locale=\"en_US\"}}")
            .apply(10));
  }

  @Test
  public void pluralize() throws IOException {
    assertEquals(
        "There are no files on disk.",
        handlebars.compile("{{pluralize this 0 \"disk\" locale=\"en_US\"}}")
            .apply(
                "There {0} on {1}.::are no files::is one file::are {0} files"));

    assertEquals(
        "There is one file on disk.",
        handlebars.compile("{{pluralize this 1 \"disk\" locale=\"en_US\"}}")
            .apply(
                "There {0} on {1}.::are no files::is one file::are {0} files"));

    assertEquals(
        "There are 1,000 files on disk.",
        handlebars.compile("{{pluralize this 1000 \"disk\" locale=\"en_US\"}}")
            .apply(
                "There {0} on {1}.::are no files::is one file::are {0} files"));
  }

  @Test
  public void slugify() throws IOException {
    assertEquals("hablo-espanol",
        handlebars.compile("{{slugify this}}")
            .apply("Hablo español"));
  }

  @Test
  public void titleize() throws IOException {
    assertEquals("Handlebars.java Rocks!",
        handlebars.compile("{{titleize this}}")
            .apply("Handlebars.java rocks!"));
  }

  @Test
  public void transliterate() throws IOException {
    assertEquals("Hablo espanol",
        handlebars.compile("{{transliterate this}}")
            .apply("Hablo español"));
  }

  @Test
  public void underscore() throws IOException {
    assertEquals("Handlebars_Java_rock",
        handlebars.compile("{{underscore this}}")
            .apply("Handlebars Java rock"));
  }

  @Test
  public void wordWrap() throws IOException {
    assertEquals("Handlebars.java",
        handlebars.compile("{{wordWrap this 14}}")
            .apply("Handlebars.java rock"));
  }
}
