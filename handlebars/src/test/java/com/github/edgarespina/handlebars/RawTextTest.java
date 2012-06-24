package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

/**
 * Unit test for {@link Template#text()}
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class RawTextTest {

  @Test
  public void plainText() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Plain Text!");
    assertEquals("Plain Text!", template.text());
  }

  @Test
  public void var() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{var}}!");
    assertEquals("hello {{var}}!", template.text());
  }

  @Test
  public void varAmp() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{& var}}!");
    assertEquals("hello {{&var}}!", template.text());
  }

  @Test
  public void var3() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{{ var }}}!");
    assertEquals("hello {{{var}}}!", template.text());
  }

  @Test
  public void emptySection() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{#section}} {{/section}}!");
    assertEquals("hello {{#section}} {{/section}}!", template.text());
  }

  @Test
  public void section() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("hello {{#section}} hello {{var}}! {{/section}}!");
    assertEquals("hello {{#section}} hello {{var}}! {{/section}}!",
        template.text());
  }

  @Test
  public void invertedEmptySection() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{^section}} {{/section}}!");
    assertEquals("hello {{^section}} {{/section}}!", template.text());
  }

  @Test
  public void invertedSection() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("hello {{^section}} hello {{var}}! {{/section}}!");
    assertEquals("hello {{^section}} hello {{var}}! {{/section}}!",
        template.text());
  }

  @Test
  public void partial() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{> user }}!");
    assertEquals("hello {{>user}}!", template.text());
  }

  @Test
  public void helper() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{dateFormat context arg0 hash=hash0}}!");
    assertEquals("hello {{dateFormat context arg0 hash=hash0}}!", template.text());
  }

  @Test
  public void blockHelper() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("hello {{#with context arg0 hash=hash}}hah{{/with}}!");
    assertEquals("hello {{#with context arg0 hash=hash}}hah{{/with}}!", template.text());
  }
}
