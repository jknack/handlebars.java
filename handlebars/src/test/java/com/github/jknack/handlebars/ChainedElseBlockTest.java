/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class ChainedElseBlockTest extends AbstractTest {

  @Test
  public void chainedInvertedSections() throws IOException {
    shouldCompileTo(
        "{{#people}}{{name}}{{else if none}}{{none}}{{/people}}",
        $("none", "No people"),
        "No people");

    shouldCompileTo(
        "{{#people}}{{name}}{{else if nothere}}fail{{else unless nothere}}{{none}}{{/people}}",
        $("none", "No people"),
        "No people");

    shouldCompileTo(
        "{{#people}}{{name}}{{else if none}}{{none}}{{else}}fail{{/people}}",
        $("none", "No people"),
        "No people");
  }

  @Test
  public void chainedInvertedSectionsWithMismatch() throws IOException {
    assertThrows(
        HandlebarsException.class,
        () ->
            shouldCompileTo(
                "{{#people}}{{name}}{{else if none}}{{none}}{{/if}}",
                $("none", "No people"),
                "No people"));
  }

  @Test
  public void blockStandaloneChainedElseSections() throws IOException {
    shouldCompileTo(
        "{{#people}}{{name}}{{else if none}}{{none}}{{/people}}",
        $("none", "No people"),
        "No people");
    shouldCompileTo(
        "{{#people}}{{name}}{{else if none}}{{none}}{{^}}{{/people}}",
        $("none", "No people"),
        "No people");
  }
}
