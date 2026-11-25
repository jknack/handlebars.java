/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue1071 extends AbstractTest {

  @Test
  public void whitespaceControlOnPartialBlock() throws IOException {
    shouldCompileToWithPartials(
        "BEGIN\n{{~#>partial_test ~}}\nMIDDLE\n{{~/partial_test ~}}\nEND",
        $,
        $("partial_test", "{{~> @partial-block ~}}"),
        "BEGINMIDDLEEND");
  }

  @Test
  public void whitespaceControlOnPartialBlockWithTrailingNewline() throws IOException {
    shouldCompileToWithPartials(
        "BEGIN\n{{~#>partial_test ~}}\nMIDDLE\n{{~/partial_test ~}}\nEND\n",
        $,
        $("partial_test", "{{~> @partial-block ~}}\n"),
        "BEGINMIDDLEEND\n");
  }
}
