package com.github.edgarespina.handlebars;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.junit.Test;

public class TemplateNotFoundTest {

  @Test(expected = FileNotFoundException.class)
  public void templateNotFound() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.compile(URI.create("template.hbs"));
  }

  @Test(expected = HandlebarsException.class)
  public void partialNotFound() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.compile("{{> text}}");
  }
}
