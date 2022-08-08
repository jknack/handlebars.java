/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

  /**
   * The plain text. Required.
   */
  private StringBuilder text;

  /** The escape's char or empty. */
  private String escapeChar;

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

  /**
   * @return Same as {@link #text()} without the escape char.
   */
  public char[] textWithoutEscapeChar() {
    return text.toString().toCharArray();
  }

  @Override
  protected void merge(final Context scope, final Writer writer) throws IOException {
    writer.write(text.toString());
  }

  /**
   * Append text.
   *
   * @param text The text to append.
   * @return This object.
   */
  public Text append(final char[] text) {
    this.text.append(text);
    return this;
  }

}
