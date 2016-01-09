package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue463 extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.parentScopeResolution(false);
  }

  @Test
  public void parentScopeResolution() throws IOException {
    shouldCompileTo("Hello {{#child}}{{value}}{{bestQB}}{{/child}}",
        $("hash", $("value", "Brett", "child", $("bestQB", "Favre"))), "Hello Favre");
  }

  @Test
  public void parentScopeResolutionDataContext() throws IOException {
    shouldCompileTo("{{#each p.list}}{{@index}}.{{title}}.{{/each}}",
        $("hash", $("p", $("list", new Object[]{$("title", "A"), $("title", "B") }))), "0.A.1.B.");
  }

}
