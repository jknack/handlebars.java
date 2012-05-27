package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.ParsingException;
import com.github.edgarespina.handlerbars.Template;

class Partial extends Template {

  private Template template;

  public Partial(final Handlebars handlebars, final String uri)
      throws ParsingException, IOException {
    template = handlebars.compile(uri.trim());
  }

  @Override
  public void merge(final Map<String, Object> scope, final Writer writer)
      throws IOException {
    template.merge(scope, writer);
  }

  @Override
  public String toString() {
    return template.toString();
  }

}
