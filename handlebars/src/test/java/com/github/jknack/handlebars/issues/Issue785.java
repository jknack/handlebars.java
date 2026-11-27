/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue785 extends AbstractTest {

  @Test
  public void shouldResolveThis() throws IOException {
    shouldCompileTo(
        "{{#each items as |item i|}}{{this.i}}: {{this.j}}\n{{/each}}",
        $("items", List.of($("i", "a", "j", "b"), $("i", 1, "j", 2))),
        "a: b\n1: 2\n");
  }
}
