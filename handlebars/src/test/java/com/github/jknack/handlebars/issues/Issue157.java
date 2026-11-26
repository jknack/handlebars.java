/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class Issue157 extends AbstractTest {

  @Test
  public void whitespacesAndSpecialCharactersInTemplateNames() throws IOException {
    Handlebars handlebars = new Handlebars();

    assertEquals("works!", handlebars.compile("space between").apply($));
  }
}
