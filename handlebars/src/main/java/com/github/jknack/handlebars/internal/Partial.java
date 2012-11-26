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
import static org.parboiled.common.Preconditions.checkNotNull;

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
   * Set the partial template.
   *
   * @param path The partial path.
   * @param template The template. Required.
   * @param context An optional context. Optional.
   * @return This partial.
   */
  public Partial template(final String path, final Template template,
      final String context) {
    this.path = checkNotNull(path, "The path is required.");
    this.template = checkNotNull(template, "The template is required.");
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
    return "{{>" + path + "}}";
  }

  @Override
  public boolean remove(final Template child) {
    return ((BaseTemplate) template).remove(child);
  }
}
