/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class GlobalDelimsTest extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    return super.newHandlebars().startDelimiter("<<").endDelimiter(">>");
  }

  @Test
  public void customDelims() throws IOException {
    shouldCompileTo("<<hello>>", $("hello", "hi"), "hi");
  }
}
