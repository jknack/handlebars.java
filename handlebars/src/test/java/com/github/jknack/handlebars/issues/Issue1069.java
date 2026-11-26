/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;

public class Issue1069 extends AbstractTest {

  @Test
  public void inlinePartialCallingItselfIsDetected() throws IOException {
    HandlebarsException ex =
        assertThrows(
            HandlebarsException.class,
            () ->
                new Handlebars()
                    .compileInline(
                        "{{#*inline \"myPartial\"}}{{> myPartial}}{{/inline}}{{> myPartial}}")
                    .apply(""));
    assertTrue(ex.getMessage().contains("infinite loop detected"), ex.getMessage());
  }

  @Test
  public void inlinePartialsCalledIndirectlyInALoopAreDetected() throws IOException {
    HandlebarsException ex =
        assertThrows(
            HandlebarsException.class,
            () ->
                new Handlebars()
                    .compileInline(
                        "{{#*inline \"a\"}}{{> b}}{{/inline}}{{#*inline \"b\"}}{{>"
                            + " a}}{{/inline}}{{> a}}")
                    .apply(""));
    assertTrue(ex.getMessage().contains("infinite loop detected"), ex.getMessage());
  }

  @Test
  public void inlinePartialCanBeCalledMoreThanOnce() throws IOException {
    shouldCompileTo(
        "{{#*inline \"x\"}}x{{/inline}}{{> x}}{{> x}}{{#*inline \"y\"}}{{> x}}{{/inline}}{{> y}}",
        $,
        "xxx");
  }

  @Test
  public void shouldAllowRecursionWhenInfiniteLoopsIsEnabled() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.setInfiniteLoops(true);
    String result =
        handlebars
            .compileInline(
                "{{#*inline \"tree\"}}{{name}}{{#each children}}({{> tree}}){{/each}}{{/inline}}{{>"
                    + " tree}}")
            .apply(
                $(
                    "name",
                    "a",
                    "children",
                    java.util.List.of($("name", "b", "children", java.util.List.of()))));
    org.junit.jupiter.api.Assertions.assertEquals("a(b)", result);
  }
}
