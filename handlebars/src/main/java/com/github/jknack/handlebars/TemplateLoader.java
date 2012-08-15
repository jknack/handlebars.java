/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

/**
 * Locate resource in a resource repository like: classpath, filesystem,
 * network, web context.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public abstract class TemplateLoader {

  /**
   * The default view prefix.
   */
  protected static final String DEFAULT_PREFIX = "/";

  /**
   * The default view suffix.
   */
  public static final String DEFAULT_SUFFIX = ".hbs";

  /**
   * The prefix that gets prepended to view names when building a URI.
   */
  private String prefix = DEFAULT_PREFIX;

  /**
   * The suffix that gets appended to view names when building a URI.
   */
  private String suffix = DEFAULT_SUFFIX;

  /**
   * Load the template from a template repository.
   *
   * @param uri The resource's uri. Required.
   * @return The requested resource.
   * @throws IOException If the resource cannot be loaded.
   */
  public Reader load(final URI uri) throws IOException {
    notNull(uri, "The uri is required.");
    notEmpty(uri.toString(), "The uri is required.");
    String location = resolve(normalize(uri.toString()));
    Reader reader = read(location);
    if (reader == null) {
      throw new FileNotFoundException(location.toString());
    }
    Handlebars.debug("Resource found: %s", location);
    return reader;
  }

  /**
   * Load the template as string from a template repository.
   *
   * @param uri The resource's uri. Required.
   * @return The requested resource.
   * @throws IOException If the resource cannot be loaded.
   */
  public String loadAsString(final URI uri) throws IOException {
    Reader reader = new BufferedReader(load(uri));
    try {
      StringBuilder buffer = new StringBuilder(1024 * 4);
      int ch;
      while ((ch = reader.read()) != -1) {
        buffer.append((char) ch);
      }
      return buffer.toString();
    } finally {
      reader.close();
    }
  }

  /**
   * Resolve the uri to an absolute location.
   *
   * @param uri The candidate uri.
   * @return Resolve the uri to an absolute location.
   */
  public String resolve(final String uri) {
    return prefix + normalize(uri) + suffix;
  }

  /**
   * Normalize the uri by removing '/' at the beginning.
   *
   * @param uri The candidate uri.
   * @return A uri without '/' at the beginning.
   */
  private String normalize(final String uri) {
    if (uri.startsWith("/")) {
      return uri.substring(1);
    }
    return uri;
  }

  /**
   * Read the resource from the given URI.
   *
   * @param location The resource's location.
   * @return The requested resource or null if not found.
   * @throws IOException If the resource cannot be loaded.
   */
  protected abstract Reader read(String location) throws IOException;

  /**
   * Set the prefix that gets prepended to view names when building a URI.
   *
   * @param prefix The prefix that gets prepended to view names when building a
   *        URI.
   */
  public void setPrefix(final String prefix) {
    checkNotNull(prefix, "A view prefix is required.");
    checkArgument(prefix.length() > 0, "A view prefix is required.");
    this.prefix = prefix;
    if (!this.prefix.endsWith("/")) {
      this.prefix += "/";
    }
  }

  /**
   * Set the suffix that gets appended to view names when building a URI.
   *
   * @param suffix The suffix that gets appended to view names when building a
   *        URI.
   */
  public void setSuffix(final String suffix) {
    checkNotNull(suffix, "A view suffix is required.");
    checkArgument(suffix.length() > 0, "A view suffix  is required.");
    this.suffix = checkNotNull(suffix, "The view suffix is required.");
  }

  /**
   * @return The prefix that gets prepended to view names when building a URI.
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * @return The suffix that gets appended to view names when building a
   *         URI.
   */
  public String getSuffix() {
    return suffix;
  }
}
