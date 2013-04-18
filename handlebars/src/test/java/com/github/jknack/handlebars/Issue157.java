package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class Issue157 extends AbstractTest {

  @Test
  public void whitespacesAndSpecialCharactersInTemplateNames() throws IOException {
    Handlebars handlebars = new Handlebars();

    assertEquals("works!", handlebars.compile("space between").apply($));
  }
}
