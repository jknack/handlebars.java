/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class InlinePartialsTest extends AbstractTest {

  @Test
  public void shouldDefineInlinePartialsForTemplate() throws IOException {
    shouldCompileTo("{{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}", $, "success");
  }

  @Test
  public void shouldOverwriteMultiplePartialsInTheSameTemplate() throws IOException {
    shouldCompileTo(
        "{{#*inline \"myPartial\"}}fail{{/inline}}{{#*inline \"myPartial\"}}success{{/inline}}{{>"
            + " myPartial}}",
        $,
        "success");
  }

  @Test
  public void shouldDefineInlinePartialsForBlock() throws IOException {
    shouldCompileTo(
        "{{#with .}}{{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}{{/with}}",
        $,
        "success");
  }

  @Test
  public void shouldDefineInlinePartialsForBlockErr() throws IOException {
    assertThrows(
        HandlebarsException.class,
        () ->
            shouldCompileTo(
                "{{#with .}}{{#*inline \"myPartial\"}}success{{/inline}}{{/with}}{{> myPartial}}",
                $,
                "success"));
  }

  @Test
  public void shouldOverrideGlobalPartials() throws IOException {
    shouldCompileToWithPartials(
        "{{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}",
        $,
        $("myPartial", "fail"),
        "success");
  }

  @Test
  public void shouldOverrideTemplatePartials() throws IOException {
    shouldCompileTo(
        "{{#*inline \"myPartial\"}}fail{{/inline}}{{#with .}}{{#*inline"
            + " \"myPartial\"}}success{{/inline}}{{> myPartial}}{{/with}}",
        $,
        "success");
  }

  @Test
  public void shouldOverridePartialsDownTheEntireStack() throws IOException {
    shouldCompileTo(
        "{{#with .}}{{#*inline \"myPartial\"}}success{{/inline}}{{#with .}}{{#with .}}{{>"
            + " myPartial}}{{/with}}{{/with}}{{/with}}",
        $,
        "success");
  }

  @Test
  public void shouldDefineInlinePartialsForPartialCall() throws IOException {
    shouldCompileToWithPartials(
        "{{#*inline \"myPartial\"}}success{{/inline}}{{> dude}}",
        $,
        $("dude", "{{> myPartial}}"),
        "success");
  }

  @Test
  public void shouldDefineInlinePartialsInPartialBlockCall() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        $,
        $("dude", "{{> myPartial}}"),
        "success");
  }

  @Test
  public void inlinePartialText() throws IOException {
    assertEquals(
        "{{#*inline \"myPartial\"}}success{{/inline}}{{>myPartial}}",
        compile("{{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}").text());
  }
}
