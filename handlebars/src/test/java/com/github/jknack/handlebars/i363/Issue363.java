/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i363;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue363 extends AbstractTest {

  @Test
  public void shouldNotDependsOnNewLine() throws IOException {
    shouldCompileTo(
        "{{model1.listOfValues1.[0]}}{{#if model3}}{{model2.users.[0].name}}{{/if}}", $, "");
  }
}
