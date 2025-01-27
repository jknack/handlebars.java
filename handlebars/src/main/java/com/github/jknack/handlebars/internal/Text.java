/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;
import java.io.Writer;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;

/**
 * Plain text template.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Text extends BaseTemplate {

  /** The plain text. Required. */
  private final StringBuilder text;

  /** The escape's char or empty. */
  private final String escapeChar;

  /**
   * Creates a new {@link Text}.
   *
   * @param handlebars A handlebars instance. Required.
   * @param text The text content. Required.
   * @param escapeChar The escape char or empty.
   */
  Text(final Handlebars handlebars, final String text, final String escapeChar) {
    super(handlebars);
    this.text = new StringBuilder(text);
    this.escapeChar = escapeChar;
  }

  /**
   * Creates a new {@link Text}.
   *
   * @param handlebars A handlebars instance. Required.
   * @param text The text content. Required.
   */
  Text(final Handlebars handlebars, final String text) {
    this(handlebars, text, "");
  }

  @Override
  public String text() {
    return escapeChar + text.toString();
  }

  @Override
  protected void merge(final Context scope, final Writer writer) throws IOException {
    writer.write(text.toString());
  }

  /**
   * Merges the content of the given {@link Text} instance into this instance.
   *
   * @param other the {@link Text} instance to merge with this instance;
   *               if null or contains no text, no action is taken
   */
  public void append(final Text other) {
    if (other != null && other.text != null) {
      this.text.append(other.text);
    }
  }
}
