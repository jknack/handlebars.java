package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class HelperMissingTest {

  /**
   * Mustache fallback.
   *
   * @throws IOException
   */
  @Test
  public void helperMissingOk() throws IOException {
    Handlebars handlebars = new Handlebars();
    assertEquals("", handlebars.compile("{{missing}}").apply(new Object()));
  }

  /**
   * Mustache fallback.
   *
   * @throws IOException
   */
  @Test
  public void blockHelperMissingOk() throws IOException {
    Handlebars handlebars = new Handlebars();
    assertEquals("",
        handlebars.compile(
            "{{#missing}}This is a mustache fallback{{/missing}}")
            .apply(new Object()));
  }

  /**
   * Handlebars syntax, it MUST fail.
   *
   * @throws IOException
   */
  @Test(expected = HandlebarsException.class)
  public void helperMissingFail() throws IOException {
    Handlebars handlebars = new Handlebars();
    assertEquals("", handlebars.compile("{{missing x}}").apply(new Object()));
  }

  @Test(expected = HandlebarsException.class)
  public void blockHelperMissingFail() throws IOException {
    Handlebars handlebars = new Handlebars();
    assertEquals("",
        handlebars.compile(
            "{{#missing x}}This is a mustache fallback{{/missing}}")
            .apply(new Object()));
  }

  @Test
  public void helperMissingOverride() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper(Handlebars.HELPER_MISSING, new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options)
          throws IOException {
        return options.fn.text();
      }
    });
    assertEquals("{{missing x}}",
        handlebars.compile("{{missing x}}").apply(new Object()));
  }

  @Test
  public void blockHelperMissingOverride() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper(Handlebars.HELPER_MISSING, new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options)
          throws IOException {
        return options.fn.text();
      }
    });
    assertEquals("Raw display",
        handlebars.compile("{{#missing x}}Raw display{{/missing}}")
            .apply(new Object()));
  }
}
