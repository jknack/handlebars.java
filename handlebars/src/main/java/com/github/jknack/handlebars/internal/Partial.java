/**
 * Copyright (c) 2012 Edgar Espina
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

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Writer;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;

/**
 * Partials begin with a greater than sign, like {{> box}}.
 * Partials are rendered at runtime (as opposed to compile time), so recursive
 * partials are possible. Just avoid infinite loops.
 * They also inherit the calling context.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Partial extends BaseTemplate {

  /**
   * The internal template.
   */
  private Template template;

  /**
   * The partial path.
   */
  private String path;

  /**
   * A partial context. Optional.
   */
  private String switchContext;

  /**
   * The start delimiter.
   */
  private String startDelimiter;

  /**
   * The end delimiter.
   */
  private String endDelimiter;

  /**
   * Set the partial template.
   *
   * @param path The partial path.
   * @param template The template. Required.
   * @param context An optional context. Optional.
   * @return This partial.
   */
  public Partial template(final String path, final Template template,
      final String context) {
    this.path = notEmpty(path, "The path is required.");
    this.template = notNull(template, "The template is required.");
    switchContext = defaultString(context, "this");
    return this;
  }

  @Override
  public void merge(final Context current, final Writer writer)
      throws IOException {
    if (switchContext.equals("this")) {
      template.apply(current, writer);
    } else {
      template.apply(current.get(switchContext), writer);
    }
  }

  @Override
  public String text() {
    return new StringBuilder(startDelimiter)
      .append('>')
      .append(path)
      .append(endDelimiter)
      .toString();
  }

  /**
   * Set the end delimiter.
   *
   * @param endDelimiter The end delimiter.
   * @return This section.
   */
  public Partial endDelimiter(final String endDelimiter) {
    this.endDelimiter = endDelimiter;
    return this;
  }

  /**
   * Set the start delimiter.
   *
   * @param startDelimiter The start delimiter.
   * @return This section.
   */
  public Partial startDelimiter(final String startDelimiter) {
    this.startDelimiter = startDelimiter;
    return this;
  }
  /**
   * The start delimiter.
   *
   * @return The start delimiter.
   */
  public String startDelimiter() {
    return startDelimiter;
  }

  /**
   * The end delimiter.
   *
   * @return The end delimiter.
   */
  public String endDelimiter() {
    return endDelimiter;
  }

}
