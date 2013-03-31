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

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;


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
public abstract class URLTemplateLoader implements TemplateLoader {

  /**
   * The prefix that gets prepended to view names when building a URI.
   */
  private String prefix = DEFAULT_PREFIX;

  /**
   * The suffix that gets appended to view names when building a URI.
   */
  private String suffix = DEFAULT_SUFFIX;

  /**
   * Get a template source for the given uri.
   *
   * @param uri The location of the template source. Required.
   * @return A new template source.
   * @throws IOException If the template's source can't be read.
   */
  @Override
  public TemplateSource sourceAt(final URI uri) throws IOException {
    notNull(uri, "The uri is required.");
    notEmpty(uri.toString(), "The uri is required.");
    String location = resolve(normalize(uri));
    URL resource = getResource(location);
    if (resource == null) {
      throw new FileNotFoundException(location);
    }
    return new URLTemplateSource(location, resource);
  }

  /**
   * Get a template resource for the given location.
   *
   * @param location The location of the template source. Required.
   * @return A new template resource.
   * @throws IOException If the template's resource can't be resolved.
   */
  protected abstract URL getResource(String location) throws IOException;

  /**
   * Resolve the uri to an absolute location.
   *
   * @param uri The candidate uri.
   * @return Resolve the uri to an absolute location.
   */
  @Override
  public String resolve(final URI uri) {
    return prefix + normalize(uri) + suffix;
  }

  /**
   * Normalize the uri by removing '/' at the beginning.
   *
   * @param uri The candidate uri.
   * @return A uri without '/' at the beginning.
   */
  protected URI normalize(final URI uri) {
    if (uri.toString().startsWith("/")) {
      return URI.create(uri.toString().substring(1));
    }
    return uri;
  }

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
  @Override
  public String getPrefix() {
    return prefix;
  }

  /**
   * @return The suffix that gets appended to view names when building a
   *         URI.
   */
  @Override
  public String getSuffix() {
    return suffix;
  }
}
