package com.github.jknack.handlebars.i404;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue404 extends AbstractTest {

  @Test
  public void shouldEscapeVarInsideQuotes() throws IOException {
    shouldCompileTo("\"\\{{var}}\"", $, "\"{{var}}\"");

    shouldCompileTo("<tag attribute=\"\\{{var}}\"/>", $, "<tag attribute=\"{{var}}\"/>");
  }
}
