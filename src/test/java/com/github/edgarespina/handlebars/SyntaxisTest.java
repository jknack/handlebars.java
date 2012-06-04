package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.ResourceLocator;
import com.github.edgarespina.handlerbars.Template;

public class SyntaxisTest {

  @Test
  public void comment() throws IOException, RecognitionException {
    Template template = compile("{{!Comment}}");
    assertEquals("", template.toString());
  }

  @Test
  public void setDelimiters() throws IOException, RecognitionException {
    Template template = compile("{{=<% %>=}}<%name%>");
    assertEquals("{{name}}", template.toString());
  }

  @Test
  public void setDelimitersSingle() throws IOException, RecognitionException {
    Template template = compile("{{=| |=}}|name|");
    assertEquals("{{name}}", template.toString());
  }

  @Test
  public void trailingSpacesInComment() throws IOException,
      RecognitionException {
    Template template = compile("  {{!Comment}} \n");
    assertEquals("", template.toString());
  }

  @Test
  public void plainText() throws IOException, RecognitionException {
    Template template = compile("Some free text");
    assertEquals("Some free text", template.toString());
  }

  @Test
  public void variable() throws IOException, RecognitionException {
    Template template = compile("{{name}}");
    assertEquals("{{name}}", template.toString());
  }

  @Test
  public void section() throws IOException, RecognitionException {
    Template template = compile("{{#person}}{{name}}{{/person}}");
    assertEquals("{{#person}}{{name}}{{/person}}", template.toString());
  }

  public Template compile(final String input) throws IOException,
      RecognitionException {
    Template template =
        new Handlebars(resourceLocator(input)).compile("template.html");
    assertNotNull(template);
    return template;
  }

  public ResourceLocator resourceLocator(final String input) {
    return new ResourceLocator() {

      @Override
      protected Reader read(final String uri) throws IOException {
        return new StringReader(input);
      }
    };
  }

}
