package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

public class Issue611 extends v4Test {

  @Test
  public void shouldScapeRightBracket() throws Exception {
    shouldCompileTo("<div class=\"entry\">\n"
        + "  <h1>{{ fields.[Special [case\\]] }}</h1>\n"
        + "</div>", $("hash", $("fields", $("Special [case\\]", "yo"))), "<div class=\"entry\">\n"
        + "  <h1>yo</h1>\n"
        + "</div>");
  }
}
