/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i664;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue664 extends AbstractTest {

  // Windows newline "\r\n" should not cause problems during compilation of
  // templates
  @Test
  public void windowsNewlineShouldNotCauseErrors() throws IOException {
    assertEquals(
        "{{#if value}}true{{else}}false{{/if}}",
        compile("{{#if\r\nvalue}}true{{else}}false{{/if}}").text());
  }
}
