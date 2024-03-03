/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue477b extends v4Test {
  private String template = null;
  private Hash data = null;
  private String expected = null;

  @BeforeEach
  public void init() {
    template = "{{> dude _greeting=\"Hello\"}}";
    data = $("hash", $("name", "Elliot"), "partials", $("dude", "{{_greeting}} {{name}}!"));
    expected = "Hello Elliot!";
  }

  @Test
  public void partialWithHashAndNoParentScopeResolution() throws IOException {
    shouldCompileTo(template, data, expected);
  }

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.setParentScopeResolution(false);
  }
}
