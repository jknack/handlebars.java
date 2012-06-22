package com.github.edgarespina.handlebars.internal;

/**
 * One of ' ', '\t', '\f', '\r\n', '\n'.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Blank extends Text {

  /**
   * Creates a new {@link Blank}.
   *
   * @param text One or more white chars.
   */
  public Blank(final String text) {
    super(text);
  }

}
