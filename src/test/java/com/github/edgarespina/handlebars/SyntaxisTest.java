package com.github.edgarespina.handlebars;

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
    Template template = compile("  {{!Comment}}  {{name}}");
    System.out.println(template);
  }


  @Test
  public void plainText() throws IOException, RecognitionException {
    Template template = compile("  {{string}}");
    System.out.println(template);
  }


  @Test
  public void singleMustache() throws IOException, RecognitionException {
    Template template = compile("{{#a}}{{b.c}}{{/a}}");
    System.out.println(template);
  }

  public Template compile(final String input) throws IOException, RecognitionException {
    return new Handlebars(resourceLocator(input)).compile("template.html");
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
