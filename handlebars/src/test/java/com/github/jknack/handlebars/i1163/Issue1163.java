/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i1163;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue1163 extends AbstractTest {

  @Test
  public void shouldNotFailOnEmptyList() throws IOException {
    shouldCompileTo("{{@last}}", List.of(), "");
    shouldCompileTo("{{@first}}", List.of(), "");
  }
}
