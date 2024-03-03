/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class TemplateNotFoundTest {

  @Test(expected = FileNotFoundException.class)
  public void templateNotFound() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.compile("template.hbs");
  }

  @Test(expected = HandlebarsException.class)
  public void partialNotFound() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.compileInline("{{> text}}").apply(null);
  }
}
