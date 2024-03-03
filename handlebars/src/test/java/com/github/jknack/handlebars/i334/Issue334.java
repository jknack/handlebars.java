/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i334;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue334 extends AbstractTest {

  @Test
  public void withHelperSpec() throws IOException {
    shouldCompileTo("{{#each this}}{{@first}}{{/each}}", $("one", 1, "two", 2), "first");
  }
}
