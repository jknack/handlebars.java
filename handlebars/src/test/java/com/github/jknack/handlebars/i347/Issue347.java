/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i347;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue347 extends AbstractTest {

  @Test
  public void shouldEscapePropOnTopLevel() throws IOException {
    shouldCompileTo("{{ this.[foo bar] }}", $("foo bar", "foo.bar"), "foo.bar");

    shouldCompileTo("{{ [foo bar] }}", $("foo bar", "foo.bar"), "foo.bar");
  }
}
