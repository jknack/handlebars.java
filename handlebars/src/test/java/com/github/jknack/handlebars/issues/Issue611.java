/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue611 extends v4Test {

  @Test
  public void shouldScapeRightBracket() throws Exception {
    shouldCompileTo(
        "<div class=\"entry\">\n" + "  <h1>{{ fields.[Special [case\\]] }}</h1>\n" + "</div>",
        $("hash", $("fields", $("Special [case\\]", "yo"))),
        "<div class=\"entry\">\n" + "  <h1>yo</h1>\n" + "</div>");
  }
}
