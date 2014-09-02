package com.github.jknack.handlebars.i310;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue310 extends AbstractTest {

  @Test
  public void commentWithClosingMustache() throws IOException {
    shouldCompileTo("{{!-- not a var}} --}}", $, "");
  }

  @Test
  public void commentNotNestable() throws IOException {
    shouldCompileTo("{{! {{not}} a var}}", $, " a var}}");
  }
}
