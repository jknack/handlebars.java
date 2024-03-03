/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Hbs519 extends AbstractTest {

  @Test
  public void shouldCompileRawHelperStatementInsideConditional() throws IOException {
    shouldCompileTo(
        "{{#unless test}}{{{{raw-helper}}}}{{testing}}{{{{/raw-helper}}}}{{/unless}}",
        $,
        $(
            "raw-helper",
            new Helper<Object>() {

              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                return options.fn();
              }
            }),
        "{{testing}}");
  }

  @Test
  public void shouldGetTextVersionOfRawHelperInsideConditional() throws IOException {
    assertEquals(
        "{{#unless test}}{{{{raw-helper}}}}{{testing}}{{{{/raw-helper}}}}{{/unless}}",
        compile("{{#unless test}}{{{{raw-helper}}}}{{testing}}{{{{/raw-helper}}}}{{/unless}}")
            .text());
  }
}
