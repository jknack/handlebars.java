package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Template;

class Partial extends Template {

  private Template template;

  public Partial(final Handlebars handlebars, final String uri)
      throws HandlebarsException, IOException {
    template = handlebars.compile(uri.trim());
  }

  @Override
  public void merge(final Scope scope, final Writer writer)
      throws IOException {
    template.merge(scope, writer);
  }

  @Override
  public String toString() {
    return template.toString();
  }

}
