package com.github.edgarespina.handlebars;

import java.io.IOException;

import org.junit.Test;

public class UnbalancedDelimitersTest {

  @Test
  public void defaultFormat() throws IOException {
    try {
      Handlebars handlebars = new Handlebars();
      handlebars.compile("{{=<% >=}}");
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

}
