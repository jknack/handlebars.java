/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue614 extends v4Test {

  @Test
  public void shouldGetTextFromElseIf() throws Exception {
    String text = compile("{{#if a}}a{{else if b}}b{{else}}c{{/if}}").text();
    assertEquals("{{#if a}}a{{else if b}}b{{else}}c{{/if}}", text);
  }
}
