/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i204;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue204 extends AbstractTest {

  @Test
  public void ifElseBlockMustBeIncludedInRawText() throws IOException {
    assertEquals(
        "{{#if true}}true{{else}}false{{/if}}",
        compile("{{#if true}}true{{else}}false{{/if}}").text());
    assertEquals(
        "{{#if true}}true{{^}}false{{/if}}", compile("{{#if true}}true{{^}}false{{/if}}").text());
  }
}
