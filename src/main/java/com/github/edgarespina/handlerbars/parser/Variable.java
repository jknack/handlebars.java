package com.github.edgarespina.handlerbars.parser;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.util.Map;

public class Variable extends Node {

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
  public void toString(final StringBuilder output,
      final Map<String, Object> scope) {
    Object value = scope.get(name);
    if (value == null) {
      value = "";
    }
    if (escape) {
      output.append(escapeHtml4(value.toString()));
    } else {
      output.append(value);
    }
  }
}
