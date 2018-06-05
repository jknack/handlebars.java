package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Issue614 extends v4Test {

  @Test
  public void shouldGetTextFromElseIf() throws Exception {
    String text = compile("{{#if a}}a{{else if b}}b{{else}}c{{/if}}").text();
    assertEquals("{{#if a}}a{{else if b}}b{{else}}c{{/if}}", text);
  }
}
