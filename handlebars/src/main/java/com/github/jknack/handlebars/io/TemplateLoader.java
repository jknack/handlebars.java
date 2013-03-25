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
package com.github.jknack.handlebars.io;

import java.io.IOException;
import java.net.URI;

import com.github.jknack.handlebars.Template;

/**
 * A template loader can find the {@link TemplateSource} for a {@link Template}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public interface TemplateLoader {

  /**
   * The default view prefix.
   */
  String DEFAULT_PREFIX = "/";

  /**
   * The default view suffix.
   */
  String DEFAULT_SUFFIX = ".hbs";

  /**
   * Get a template source for the given uri.
   *
   * @param uri The location of the template source. Required.
   * @return A new template source.
   * @throws IOException If the template's source can't be read.
   */
  TemplateSource sourceAt(final URI uri) throws IOException;

  /**
   * Resolve the uri to an absolute location.
   *
   * @param uri The candidate uri.
   * @return Resolve the uri to an absolute location.
   */
  String resolve(final URI uri);

  /**
   * @return The prefix that gets prepended to view names when building a URI.
   */
  String getPrefix();

  /**
   * @return The suffix that gets appended to view names when building a
   *         URI.
   */
  String getSuffix();

}
