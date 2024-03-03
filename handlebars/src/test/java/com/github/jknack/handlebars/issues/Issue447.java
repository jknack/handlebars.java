/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue447 extends v4Test {

  @Test
  public void levels() throws IOException {
    shouldCompileTo("{{log 'message'}}", $, "");

    shouldCompileTo("{{log 'a' 'b' 'c'}}", $, "");

    shouldCompileTo("{{log 'message' level='info'}}", $, "");

    shouldCompileTo("{{log 'message' level='debug'}}", $, "");

    shouldCompileTo("{{log 'message' level='error'}}", $, "");

    shouldCompileTo("{{log 'message' level='trace'}}", $, "");
  }

  @Test
  public void logFn() throws IOException {
    shouldCompileTo("{{#log}}Name: {{name}}{{/log}}", $("hash", $("name", "John")), "");
  }
}
