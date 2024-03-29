/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

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

  @Test
  public void helperMissingName() throws IOException {
    shouldCompileTo(
        "{{varx 7}}",
        $,
        $(
            HelperRegistry.HELPER_MISSING,
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                return options.helperName;
              }
            }),
        "varx");
  }

  @Test
  public void helperBlockMissingName() throws IOException {
    shouldCompileTo(
        "{{#varz 7}}{{/varz}}",
        $,
        $(
            HelperRegistry.HELPER_MISSING,
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                return options.helperName;
              }
            }),
        "varz");
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
  @Test
  public void helperMissingFail() {
    assertThrows(
        HandlebarsException.class,
        () -> shouldCompileTo("{{missing x}}", new Object(), "must fail"));
  }

  @Test
  public void blockHelperMissingFail() {
    assertThrows(
        HandlebarsException.class,
        () ->
            shouldCompileTo(
                "{{#missing x}}This is a mustache fallback{{/missing}}",
                new Object(),
                "must fail"));
  }

  @Test
  public void helperMissingOverride() throws IOException {
    Hash helpers =
        $(
            HelperRegistry.HELPER_MISSING,
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                return "empty";
              }
            });
    shouldCompileTo("{{missing x}}", new Object(), helpers, "empty");
  }

  @Test
  public void blockHelperMissingOverride() throws IOException {
    Hash helpers =
        $(
            HelperRegistry.HELPER_MISSING,
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                return options.fn.text();
              }
            });
    shouldCompileTo("{{#missing x}}Raw display{{/missing}}", new Object(), helpers, "Raw display");
  }
}
