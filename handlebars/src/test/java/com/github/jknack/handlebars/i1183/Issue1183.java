/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i1183;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Helper;

class Issue1183 extends AbstractTest {
  @Test
  void shouldInvokeHelperWithSimpleParameter() throws IOException {
    shouldCompileTo(
        """
        {{#each names as |name|}}
          {{test.helper name}}
        {{/each}}\
        """,
        $("names", List.of("John", "Jeff")),
        $("test.helper", (Helper<Object>) (text, options) -> "Hello " + text),
        "\n" + "  Hello John\n" + "\n" + "  Hello Jeff\n");
  }
}
