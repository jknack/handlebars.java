/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue812 extends AbstractTest {

  @Test
  public void shouldAcceptNullContext() throws IOException {
    shouldCompileToWithPartials(
        "{{> partial null b=map.b}}\n{{> partial null c=map.c}}",
        $("a", "aData", "map", $("b", "bData", "c", "cData")),
        $(
            "partial",
            "{{#with a}}\n"
                + "  a: {{.}}\n"
                + "{{/with}}\n"
                + "{{#with b}}\n"
                + "  b: {{.}}\n"
                + "{{/with}}\n"
                + "{{#with c}}\n"
                + "  c: {{.}}\n"
                + "{{/with}}"),
        "\n" + "\n" + "  b: bData\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "  c: cData\n");
  }
}
