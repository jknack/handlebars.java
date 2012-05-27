package com.github.edgarespina.handlerbars.parser;

import static com.github.edgarespina.handlerbars.Handlebars.safeString;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.github.edgarespina.handlerbars.Template;

class Variable extends Template {

  private String name;

  private boolean escape;

  public Variable(final String name, final boolean escape) {
    this.name = name;
    this.escape = escape;
  }

  public String name() {
    return name;
  }

  @Override
  public void merge(final Map<String, Object> scope,
      final Writer writer) throws IOException {
    Object value = scope.get(name);
    if (value == null) {
      value = "";
    }
    // TODO: Add formatter hook
    String valueAsString =
        value instanceof String ? (String) value : value.toString();
    if (escape) {
      writer.append(safeString(valueAsString));
    } else {
      writer.append(valueAsString);
    }
  }

  @Override
  public String toString() {
    return "{{" + name + "}}";
  }
}
