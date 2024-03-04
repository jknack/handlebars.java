/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.caffeine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Decorates an existing TemplateLoader with a GuavaCache. This is useful to avoid constantly
 * creating TemplateSources.
 *
 * @author agent
 */
public class CaffeineTemplateLoader implements TemplateLoader {

  /** never null. */
  private final TemplateLoader delegate;

  /** never null. */
  private final Cache<String, TemplateSource> cache;

  /**
   * @param delegate wrappped template loader.
   * @param cache Guava Cache.
   */
  public CaffeineTemplateLoader(
      final TemplateLoader delegate, final Cache<String, TemplateSource> cache) {
    this.delegate = delegate;
    this.cache = cache;
  }

  /** {@inheritDoc} */
  public TemplateSource sourceAt(final String location) {
    return cache.get(location, loadTemplate());
  }

  /** {@inheritDoc} */
  public String resolve(final String location) {
    return delegate.resolve(location);
  }

  /** {@inheritDoc} */
  public String getPrefix() {
    return delegate.getPrefix();
  }

  /** {@inheritDoc} */
  public String getSuffix() {
    return delegate.getSuffix();
  }

  /** {@inheritDoc} */
  public void setPrefix(final String prefix) {
    delegate.setPrefix(prefix);
  }

  /** {@inheritDoc} */
  public void setSuffix(final String suffix) {
    delegate.setSuffix(suffix);
  }

  @Override
  public void setCharset(final Charset charset) {
    delegate.setCharset(charset);
  }

  @Override
  public Charset getCharset() {
    return delegate.getCharset();
  }

  private Function<String, TemplateSource> loadTemplate() {
    return path -> {
      try {
        return delegate.sourceAt(path);
      } catch (IOException ex) {
        throw Handlebars.Utils.propagate(ex);
      }
    };
  }
}
