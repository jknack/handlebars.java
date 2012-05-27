package com.github.edgarespina.handlerbars.parser;

import java.util.Map;

public class Text extends Node {

  private String text;

  public Text(final String text) {
    this.text = text;
  }

  public String text() {
    return text;
  }

  @Override
  public void toString(final StringBuilder output, final Map<String, Object> scope) {
    output.append(text);
  }
}
