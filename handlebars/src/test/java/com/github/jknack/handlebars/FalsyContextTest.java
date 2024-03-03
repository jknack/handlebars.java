/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class FalsyContextTest extends AbstractTest {

  @Test
  public void nullContext() throws IOException {
    shouldCompileTo("Hello {{world}}!", null, "Hello !");
  }

  @Test
  public void emptyContext() throws IOException {
    shouldCompileTo("Hello {{world}}!", new Object(), "Hello !");
  }

  @Test
  public void emptyMapContext() throws IOException {
    shouldCompileTo("Hello {{world}}!", Collections.emptyMap(), "Hello !");
  }

  @Test
  public void emptyList() throws IOException {
    shouldCompileTo("Hello {{world}}!", Collections.emptyList(), "Hello !");
  }

  @Test
  public void anyContext() throws IOException {
    shouldCompileTo("Hello {{world}}!", true, "Hello !");
    shouldCompileTo("Hello {{world}}!", 13.4, "Hello !");
  }
}
