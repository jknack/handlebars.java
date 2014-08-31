package com.github.jknack.handlebars.i150;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsException;

public class Issue150 extends AbstractTest {

  @Test
  public void dotNotationForArrayAccessWithDots() throws Exception {
    shouldCompileTo("{{foo.0.}}", $("foo", new Object[]{"bar" }), "bar");
  }

  @Test
  public void dotNotationForArrayAccessWithBrackets() throws Exception {
    shouldCompileTo("{{foo.[0]}}", $("foo", new Object[]{"bar" }), "bar");
  }

  @Test(expected = HandlebarsException.class)
  public void invalidBracketSyntax() throws Exception {
    shouldCompileTo("{{foo[0]}}", $("foo", new Object[]{"bar" }), "bar");
  }

  @Test
  public void invalidDotSyntax() throws Exception {
    try {
      shouldCompileTo("{{foo.0}}", $("foo", new Object[]{"bar" }), "bar");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
