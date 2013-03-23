/**
 * Copyright (c) 2012-2013 Edgar Espina
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

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Writer;

import com.github.jknack.handlebars.Context;

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
  private String text;

  /**
   * Creates a new {@link Text}.
   *
   * @param text The text content. Required.
   */
  public Text(final String text) {
    this.text = notNull(text, "The text content is required.");
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

  /**
   * Append text.
   *
   * @param text The text to append.
   * @return This object.
   */
  public Text append(final String text) {
    this.text += text;
    return this;
  }

}
