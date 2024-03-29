/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i248;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public class Issue248 {

  @Test
  public void defaultEscape() throws IOException {
    Template template = new Handlebars().compileInline("{{this}}");

    assertEquals("&quot;Escaping&quot;", template.apply("\"Escaping\""));
    assertEquals("", template.apply(null));
    assertEquals("", template.apply(""));
  }

  @Test
  public void csvEscape() throws IOException {
    Template template = new Handlebars().with(EscapingStrategy.CSV).compileInline("{{this}}");

    assertEquals("\"\"\"Escaping\"\"\"", template.apply("\"Escaping\""));
    assertEquals("", template.apply(null));
    assertEquals("", template.apply(""));
  }

  @Test
  public void xmlEscape() throws IOException {
    Template template = new Handlebars().with(EscapingStrategy.XML).compileInline("{{this}}");

    assertEquals("&lt;xml&gt;", template.apply("<xml>"));
    assertEquals("", template.apply(null));
    assertEquals("", template.apply(""));
  }

  @Test
  public void jsEscape() throws IOException {
    Template template = new Handlebars().with(EscapingStrategy.JS).compileInline("{{this}}");

    assertEquals("\\'javascript\\'", template.apply("'javascript'"));
    assertEquals("", template.apply(null));
    assertEquals("", template.apply(""));
  }
}
