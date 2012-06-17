package com.github.edgarespina.handlerbars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;

public class FalsyContextTest {

  @Test
  public void nullContext() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(null));
  }

  @Test
  public void emptyContext() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(new Object()));
  }

  @Test
  public void emptyMapContext() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(Collections.emptyMap()));
  }

  @Test
  public void emptyList() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(Collections.emptyList()));
  }

  @Test
  public void anyContext() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(true));
  }
}
