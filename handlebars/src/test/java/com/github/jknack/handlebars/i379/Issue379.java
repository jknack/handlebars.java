/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i379;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue379 extends AbstractTest {

  @Test
  public void shouldCompile379() throws IOException {
    shouldCompileTo("<div> [a:b] </div>", $, "<div> [a:b] </div>");
  }

  @Test
  public void shouldRenderAcolonB() throws IOException {
    shouldCompileTo("<div> {{[a:b]}} </div>", $("a:b", "a"), "<div> a </div>");
  }
}
