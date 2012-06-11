package com.github.edgarespina.handlerbars.parser;

import static com.github.edgarespina.handlerbars.Handlebars.safeString;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Template;

class Variable extends BaseTemplate {

  private final String name;

  private final boolean escape;

  private final Handlebars handlebars;

  private final Object value;

  public Variable(final Handlebars handlebars, final String name,
      final boolean escape) {
    this(handlebars, name, null, escape);
  }

  public Variable(final Handlebars handlebars, final String name,
      final Object value, final boolean escape) {
    this.name = name.trim();
    this.value = value;
    this.escape = escape;
    this.handlebars = handlebars;
  }

  public String name() {
    return name;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void merge(final Scope scope, final Writer writer) throws IOException {
    Object value = this.value == null ? scope.get(name) : this.value;
    if (value != null) {
      if (value instanceof Lambda) {
        value = Lambdas.merge(handlebars, (Lambda<Object>) value, scope, this);
      }
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
  public String text() {
    return "{{" + name + "}}";
  }
}
