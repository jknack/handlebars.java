/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue1114 extends AbstractTest {

  @Test
  public void shouldNotFailOnExtraCurly() throws IOException {
    shouldCompileTo("{{value}}}", $("value", "foo"), "foo}");
  }
}
