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
package com.github.jknack.handlebars.io;

import java.io.IOException;

/**
 * <p>
 * Strategy interface for loading resources from class path, file system, etc.
 * </p>
 * <h3>Templates prefix and suffix</h3>
 * <p>
 * A <code>TemplateLoader</code> provides two important properties:
 * </p>
 * <ul>
 * <li>prefix: useful for setting a default prefix where templates are stored.</li>
 * <li>suffix: useful for setting a default suffix or file extension for your templates. Default is:
 * <code>'.hbs'</code></li>
 * </ul>
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
   * Get a template source from location.
   *
   * @param location The location of the template source. Required.
   * @return A new template source.
   * @throws IOException If the template's source can't be resolved.
   */
  TemplateSource sourceAt(final String location) throws IOException;

  /**
   * Resolve a relative location to an absolute location.
   *
   * @param location The candidate location.
   * @return Resolve the uri to an absolute location.
   */
  String resolve(final String location);

  /**
   * @return The prefix that gets prepended to view names when building a URI.
   */
  String getPrefix();

  /**
   * @return The suffix that gets appended to view names when building a URI.
   */
  String getSuffix();

  /**
   * Set the prefix that gets prepended to view names when building a URI.
   *
   * @param prefix The prefix that gets prepended to view names when building a
   *        URI.
   */
  void setPrefix(final String prefix);

  /**
   * Set the suffix that gets appended to view names when building a URI.
   *
   * @param suffix The suffix that gets appended to view names when building a
   *        URI.
   */
  void setSuffix(final String suffix);

}
