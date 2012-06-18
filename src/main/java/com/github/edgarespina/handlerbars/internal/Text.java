package com.github.edgarespina.handlerbars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Template;

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
  public String rawText() {
    return text;
  }

  @Override
  public void doApply(final Context scope, final Writer writer)
      throws IOException {
    writer.append(text);
  }

  @Override
  public boolean remove(final Template child) {
    return false;
  }
}
