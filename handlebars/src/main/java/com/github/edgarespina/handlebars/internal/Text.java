package com.github.edgarespina.handlebars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlebars.Context;
import com.github.edgarespina.handlebars.Template;

/**
 * Plain text template.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Text extends BaseTemplate {

  /**
   * The plain text. Required.
   */
  private final String text;

  /**
   * Creates a new {@link Text}.
   *
   * @param text The text content. Required.
   */
  public Text(final String text) {
    this.text = checkNotNull(text, "The text content is required.");
  }

  @Override
  public String text() {
    return text;
  }

  @Override
  protected void merge(final Context scope, final Writer writer)
      throws IOException {
    writer.append(text);
  }

  @Override
  public boolean remove(final Template child) {
    return false;
  }
}
