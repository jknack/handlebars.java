package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

public class Issue573 extends v4Test {

  @Test
  public void shouldSupportNumberExpression() throws Exception {
    shouldCompileTo("{{#if 1.5}}OK{{/if}}", $, "OK");
    shouldCompileTo("{{#if 0.5}}OK{{/if}}", $, "OK");
    shouldCompileTo("{{#if .6}}OK{{/if}}", $, "OK");
  }
}
