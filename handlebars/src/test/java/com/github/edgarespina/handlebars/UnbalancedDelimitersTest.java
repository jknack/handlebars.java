package com.github.edgarespina.handlebars;

import java.io.IOException;

import org.junit.Test;

public class UnbalancedDelimitersTest {

  @Test(expected = HandlebarsException.class)
  public void defaultFormat() throws IOException {
      Handlebars handlebars = new Handlebars();
      handlebars.compile("{{=<% >=}}");
  }

}
