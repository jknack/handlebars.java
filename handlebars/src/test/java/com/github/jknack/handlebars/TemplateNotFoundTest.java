/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class TemplateNotFoundTest {

  @Test
  public void templateNotFound() {
    assertThrows(
        FileNotFoundException.class,
        () -> {
          Handlebars handlebars = new Handlebars();
          handlebars.compile("template.hbs");
        });
  }

  @Test
  public void partialNotFound() {
    assertThrows(
        HandlebarsException.class,
        () -> {
          Handlebars handlebars = new Handlebars();
          handlebars.compileInline("{{> text}}").apply(null);
        });
  }
}
