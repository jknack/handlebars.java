/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class LambdaTest extends AbstractTest {

  @Test
  public void withoutStringResult() throws IOException {
    shouldCompileTo(
        "{{lambda}}",
        $(
            "lambda",
            new Lambda<Object, Number>() {
              @Override
              public Number apply(final Object context, final Template template)
                  throws IOException {
                return 3.5D;
              }
            }),
        "3.5");
  }

  @Test
  public void withStringResult() throws IOException {
    shouldCompileTo(
        "{{lambda}}",
        $(
            "lambda",
            new Lambda<Object, String>() {
              @Override
              public String apply(final Object context, final Template template)
                  throws IOException {
                return "{{name}}";
              }
            },
            "name",
            "lambda!"),
        "lambda!");
  }
}
