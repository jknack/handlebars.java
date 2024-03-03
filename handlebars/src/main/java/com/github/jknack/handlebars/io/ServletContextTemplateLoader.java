/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;

/**
 * Load templates from the {@link ServletContext}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ServletContextTemplateLoader extends URLTemplateLoader {

  /** The servlet context. Required. */
  private final ServletContext servletContext;

  /**
   * Creates a new {@link ServletContextTemplateLoader}.
   *
   * @param servletContext The servlet context. Required.
   * @param prefix The prefix that gets prepended to view names when building a URI.
   * @param suffix The suffix that gets appended to view names when building a URI. Required.
   */
  public ServletContextTemplateLoader(
      final ServletContext servletContext, final String prefix, final String suffix) {
    this.servletContext = notNull(servletContext, "The servlet context is required.");
    setPrefix(prefix);
    setSuffix(suffix);
  }

  /**
   * Creates a new {@link ServletContextTemplateLoader}.
   *
   * @param servletContext The servlet context. Required.
   * @param prefix The prefix that gets prepended to view names when building a URI.
   */
  public ServletContextTemplateLoader(final ServletContext servletContext, final String prefix) {
    this(servletContext, prefix, DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link ServletContextTemplateLoader}.
   *
   * @param servletContext The servlet context. Required.
   */
  public ServletContextTemplateLoader(final ServletContext servletContext) {
    this(servletContext, "/", DEFAULT_SUFFIX);
  }

  @Override
  protected URL getResource(final String location) throws IOException {
    return servletContext.getResource(location);
  }
}
