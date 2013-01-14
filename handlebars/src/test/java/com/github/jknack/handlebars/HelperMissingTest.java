package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.internal.AbstractOptions;

public class HelperMissingTest extends AbstractTest {

  /**
   * Mustache fallback.
   *
   * @throws IOException
   */
  @Test
  public void helperMissingOk() throws IOException {
    shouldCompileTo("{{missing}}", new Object(), "");
  }

  /**
   * Mustache fallback.
   *
   * @throws IOException
   */
  @Test
  public void blockHelperMissingOk() throws IOException {
    shouldCompileTo("{{#missing}}This is a mustache fallback{{/missing}}", new Object(), "");
  }

  /**
   * Handlebars syntax, it MUST fail.
   *
   * @throws IOException
   */
  @Test(expected = HandlebarsException.class)
  public void helperMissingFail() throws IOException {
    shouldCompileTo("{{missing x}}", new Object(), "must fail");
  }

  @Test(expected = HandlebarsException.class)
  public void blockHelperMissingFail() throws IOException {
    shouldCompileTo("{{#missing x}}This is a mustache fallback{{/missing}}", new Object(),
        "must fail");
  }

  @Test
  public void helperMissingOverride() throws IOException {
    Hash helpers = $(Handlebars.HELPER_MISSING, new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options)
          throws IOException {
        return options.getFn().text();
      }
    });
    shouldCompileTo("{{missing x}}", new Object(), helpers, "{{missing x}}");
  }

  @Test
  public void blockHelperMissingOverride() throws IOException {
    Hash helpers = $(Handlebars.HELPER_MISSING, new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options)
          throws IOException {
        return options.getFn().text();
      }
    });
    shouldCompileTo("{{#missing x}}Raw display{{/missing}}", new Object(), helpers, "Raw display");
  }
}
