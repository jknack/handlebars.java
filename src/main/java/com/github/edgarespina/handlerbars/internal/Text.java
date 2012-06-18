package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Template;

class Text extends BaseTemplate {

  private String text;

  public Text(final String text) {
    this.text = text;
  }

  @Override
  public String rawText() {
    return text;
  }

  @Override
  public void doApply(final Context scope, final Writer writer) throws IOException {
    writer.append(text);
  }

  @Override
  public boolean remove(final Template child) {
    return false;
  }
}
