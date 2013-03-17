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

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

/**
 * <p>
 * Strategy interface for loading resources (i.e class path or file system resources)
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
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 * TemplateLoader loader = new ClassPathTemplateLoader();
 * loader.setPrefix("/templates");
 * loader.setSuffix(".html");
 * Handlebars handlebars = new Handlebars(loader);
 *
 * Template template = handlebars.compile(URI.create("mytemplate"));
 *
 * System.out.println(template.apply("Handlebars.java"));
 * </pre>
 *
 * <p>
 * The template loader resolve <code>mytemplate</code> to <code>/templates/mytemplate.html</code>
 * and load it.
 * </p>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public abstract class TemplateLoader {

  /**
   * The default view prefix.
   */
  public static final String DEFAULT_PREFIX = "/";

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
    Handlebars.debug("Loading resource: %s", location);
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
    final int bufferSize = 1024;
    try {
      char[] cbuf = new char[bufferSize];
      StringBuilder sb = new StringBuilder(bufferSize);
      int len;
      while ((len = reader.read(cbuf, 0, bufferSize)) != -1) {
        sb.append(cbuf, 0, len);
      }
      return sb.toString();
    } finally {
      if (reader != null) {
        reader.close();
      }
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
    this.prefix = notNull(prefix, "A view prefix is required.");
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
    this.suffix = defaultString(suffix, "");
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
