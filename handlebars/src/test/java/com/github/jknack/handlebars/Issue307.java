/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue307 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers(new File("src/test/resources/issue307.js"));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  public void dowork() throws IOException {
    shouldCompileTo(
        "{{#dowork root/results}}name:{{name}}, age:{{age}}{{/dowork}}",
        $(
            "root",
            $(
                "results",
                new Object[] {$("name", "edgar", "age", 34), $("name", "pato", "age", 34)})),
        "name:pato, age:34");
  }
}
