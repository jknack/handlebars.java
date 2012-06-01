package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Template;

class Text extends Template {

  private String text;

  public Text(final String text) {
    this.text = text;
  }

  public String text() {
    return text;
  }

  @Override
  public void merge(final Scope scope, final Writer writer) throws IOException {
    writer.append(text);
  }

  @Override
  public String toString() {
    return text;
  }
}
