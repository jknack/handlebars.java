package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

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
  public void merge(final Map<String, Object> scope, final Writer writer) throws IOException {
    writer.append(text);
  }

  @Override
  public String toString() {
    return text;
  }
}
