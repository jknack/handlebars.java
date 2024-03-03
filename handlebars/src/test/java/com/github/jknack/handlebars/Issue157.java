/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue157 extends AbstractTest {

  @Test
  public void whitespacesAndSpecialCharactersInTemplateNames() throws IOException {
    Handlebars handlebars = new Handlebars();

    assertEquals("works!", handlebars.compile("space between").apply($));
  }
}
