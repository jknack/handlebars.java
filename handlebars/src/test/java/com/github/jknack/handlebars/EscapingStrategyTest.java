/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class EscapingStrategyTest {
  @Test
  public void shouldEscapeVariableForJSON() throws IOException {
    Template template = new Handlebars().with(EscapingStrategy.JSON).compileInline("{{this}}");

    assertEquals("\\\"", template.apply("\""));
    assertEquals("\\\\", template.apply("\\"));
    assertEquals("\\/", template.apply("/"));
  }
}
