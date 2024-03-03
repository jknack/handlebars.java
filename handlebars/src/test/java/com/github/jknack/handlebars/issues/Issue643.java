/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue643 extends v4Test {

  @Test
  public void shouldAllowES6LetOrConstLiterals() throws Exception {
    shouldCompileTo(
        "template: {{empty}} " + "{{> partial}}",
        $("partials", $("partial", "partial: {{empty}}"), "hash", $("empty", false)),
        "template: false partial: false");
  }
}
