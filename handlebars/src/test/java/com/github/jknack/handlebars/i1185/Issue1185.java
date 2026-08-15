/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i1185;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue1185 extends AbstractTest {

  @Test
  void shouldPropagateIterationVariablesToPartial() throws IOException {
    shouldCompileToWithPartials(
        "{{#each items}}{{> myPartial}}{{/each}}",
        $("items", List.of("Item 1")),
        $("myPartial", "@index: {{@index}}, @first: {{@first}}, @last: {{@last}}"),
        "@index: 0, @first: first, @last: last");
  }
}
