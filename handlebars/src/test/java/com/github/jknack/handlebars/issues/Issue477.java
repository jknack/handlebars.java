package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue477 extends v4Test {
  private String template = null;
  private Hash data = null;
  private String expected = null;

  @Before
  public void init() {
    template = "{{> dude _greeting=\"Hello\"}}";
    data = $("hash", $("name", "Elliot"), "partials", $("dude", "{{_greeting}} {{name}}!"));
    expected = "Hello Elliot!";
  }

  @Test
  public void partialWithHash() throws IOException {
    shouldCompileTo(template, data, expected);
  }

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.setParentScopeResolution(true);
  }
}
