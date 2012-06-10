package com.github.edgarespina.handlerbars.parser;

import static com.github.edgarespina.handlerbars.Handlebars.safeString;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Template;

class Variable extends BaseTemplate {

  private final String name;

  private final boolean escape;

  public Variable(final String name, final boolean escape) {
    this.name = name.trim();
    this.escape = escape;
  }

  public String name() {
    return name;
  }

  @Override
  public void merge(final Scope scope, final Writer writer) throws IOException {
    Object value = scope.get(name);
    if (value != null) {
      boolean isString =
          value instanceof CharSequence || value instanceof Character;
      String valueAsString = value.toString();
      // TODO: Add formatter hook
      if (isString) {
        if (escape) {
          writer.append(safeString(valueAsString));
        } else {
          writer.append(valueAsString);
        }
      } else {
        // DON'T escape none String values.
        writer.append(valueAsString);
      }
    }
  }

  @Override
  public boolean remove(final Template child) {
    return false;
  }

  @Override
  public String toString() {
    return "{{" + name + "}}";
  }
}
