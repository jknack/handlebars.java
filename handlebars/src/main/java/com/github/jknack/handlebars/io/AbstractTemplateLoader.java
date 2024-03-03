/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.Validate.notNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
 * @since 1.0.0
 */
public abstract class AbstractTemplateLoader implements TemplateLoader {

  /** The prefix that gets prepended to view names when building a URI. */
  private String prefix = DEFAULT_PREFIX;

  /** The suffix that gets appended to view names when building a URI. */
  private String suffix = DEFAULT_SUFFIX;

  /** Charset. */
  private Charset charset;

  /**
   * Resolve the uri to an absolute location.
   *
   * @param uri The candidate uri.
   * @return Resolve the uri to an absolute location.
   */
  @Override
  public String resolve(final String uri) {
    return prefix + normalize(uri) + suffix;
  }

  /**
   * Normalize the location by removing '/' at the beginning.
   *
   * @param location The candidate location.
   * @return A location without '/' at the beginning.
   */
  protected String normalize(final String location) {
    if (location.toString().startsWith("/")) {
      return location.substring(1);
    }
    return location;
  }

  /**
   * Set the prefix that gets prepended to view names when building a URI.
   *
   * @param prefix The prefix that gets prepended to view names when building a URI.
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
   * @param suffix The suffix that gets appended to view names when building a URI.
   */
  public void setSuffix(final String suffix) {
    this.suffix = defaultString(suffix, "");
  }

  @Override
  public void setCharset(final Charset charset) {
    this.charset = notNull(charset, "Charset required.");
  }

  @Override
  public Charset getCharset() {
    return charset == null ? StandardCharsets.UTF_8 : charset;
  }

  /**
   * @return The prefix that gets prepended to view names when building a URI.
   */
  @Override
  public String getPrefix() {
    return prefix;
  }

  /**
   * @return The suffix that gets appended to view names when building a URI.
   */
  @Override
  public String getSuffix() {
    return suffix;
  }
}
