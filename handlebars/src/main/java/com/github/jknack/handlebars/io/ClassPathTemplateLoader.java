/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import java.net.URL;

/**
 * Load templates from the class-path. A base path can be specified at creation time. By default all
 * the templates are loaded from '/' (a.k.a. root classpath).
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ClassPathTemplateLoader extends URLTemplateLoader {

  /**
   * Creates a new {@link ClassPathTemplateLoader}.
   *
   * @param prefix The view prefix. Required.
   * @param suffix The view suffix. Required.
   */
  public ClassPathTemplateLoader(final String prefix, final String suffix) {
    setPrefix(prefix);
    setSuffix(suffix);
  }

  /**
   * Creates a new {@link ClassPathTemplateLoader}.
   *
   * @param prefix The view prefix. Required.
   */
  public ClassPathTemplateLoader(final String prefix) {
    this(prefix, DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link ClassPathTemplateLoader}. It looks for templates stored in the root of the
   * classpath.
   */
  public ClassPathTemplateLoader() {
    this("/");
  }

  @Override
  protected URL getResource(final String location) {
    return getClass().getResource(location);
  }
}
