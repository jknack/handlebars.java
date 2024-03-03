/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Strategy interface for loading resources from class path, file system, etc.
 *
 * <h3>Templates prefix and suffix</h3>
 *
 * <p>A <code>TemplateLoader</code> provides two important properties:
 *
 * <ul>
 *   <li>prefix: useful for setting a default prefix where templates are stored.
 *   <li>suffix: useful for setting a default suffix or file extension for your templates. Default
 *       is: <code>'.hbs'</code>
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public interface TemplateLoader {

  /** The default view prefix. */
  String DEFAULT_PREFIX = "/";

  /** The default view suffix. */
  String DEFAULT_SUFFIX = ".hbs";

  /**
   * Get a template source from location.
   *
   * @param location The location of the template source. Required.
   * @return A new template source.
   * @throws IOException If the template's source can't be resolved.
   */
  TemplateSource sourceAt(String location) throws IOException;

  /**
   * Resolve a relative location to an absolute location.
   *
   * @param location The candidate location.
   * @return Resolve the uri to an absolute location.
   */
  String resolve(String location);

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
   * @param prefix The prefix that gets prepended to view names when building a URI.
   */
  void setPrefix(String prefix);

  /**
   * Set the suffix that gets appended to view names when building a URI.
   *
   * @param suffix The suffix that gets appended to view names when building a URI.
   */
  void setSuffix(String suffix);

  /**
   * Set the default charset.
   *
   * @param charset Charset.
   */
  void setCharset(Charset charset);

  /**
   * @return Charset.
   */
  Charset getCharset();
}
