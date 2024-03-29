/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue319 extends AbstractTest {

  @Test
  public void blockHash() throws IOException {
    shouldCompileTo(
        "{{#partial \"hello\"}}Hello, {{name}}{{/partial}}"
            + "{{#block \"hello\" name=\"Fred\"}}{{/block}}",
        $,
        "Hello, Fred");
  }

  @Test
  public void noPartial() throws IOException {
    shouldCompileTo(
        "{{#block \"hello\" name=\"Fred\"}}Hello, {{name}}{{/block}}", $, "Hello, Fred");
  }

  @Test
  public void emptyPartialWithHash() throws IOException {
    shouldCompileTo(
        "{{#partial \"hello\" name=\"Edgar\"}}{{/partial}}"
            + "{{#block \"hello\" name=\"Fred\"}}Hello, {{name}}{{/block}}",
        $,
        "Hello, Edgar");
  }

  @Test
  public void emptyPartialWithHash2() throws IOException {
    shouldCompileTo(
        "{{partial \"hello\" name=\"Edgar\"}}"
            + "{{#block \"hello\" name=\"Fred\"}}Hello, {{name}}{{/block}}",
        $,
        "Hello, Edgar");
  }

  @Test
  public void emptyPartialWithHash3() throws IOException {
    shouldCompileTo(
        "{{{partial \"hello\" name=\"Edgar\"}}}"
            + "{{#block \"hello\" name=\"Fred\"}}Hello, {{name}}{{/block}}",
        $,
        "Hello, Edgar");
  }

  @Test
  public void emptyPartialWithHash4() throws IOException {
    shouldCompileTo(
        "{{&partial \"hello\" name=\"Edgar\"}}"
            + "{{#block \"hello\" name=\"Fred\"}}Hello, {{name}}{{/block}}",
        $,
        "Hello, Edgar");
  }
}
