/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.cache;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ReloadableTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.google.common.cache.Cache;

/**
 * An implementation of {@link TemplateCache} built on top of Guava. If {@link #setReload(boolean)}
 * is <code>on</code> we recommended one of the available auto-eviction policy of Guava, it helps to
 * reduce leaks when auto-reload is <code>on</code>.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public class GuavaTemplateCache implements TemplateCache {

  /** The guava cache. */
  private final Cache<TemplateSource, Template> cache;

  /** Turn on/off auto reloading of templates. */
  private boolean reload;

  /**
   * Creates a new {@link GuavaTemplateCache}.
   *
   * @param cache The guava cache to use. Required.
   */
  public GuavaTemplateCache(final Cache<TemplateSource, Template> cache) {
    this.cache = requireNonNull(cache, "The cache is required.");
  }

  @Override
  public void clear() {
    cache.invalidateAll();
  }

  @Override
  public void evict(final TemplateSource source) {
    cache.invalidate(key(source));
  }

  @Override
  public Template get(final TemplateSource source, final Parser parser) throws IOException {
    requireNonNull(source, "The source is required.");
    requireNonNull(parser, "The parser is required.");
    try {
      return cache.get(key(source), () -> parser.parse(source));
    } catch (ExecutionException ex) {
      throw launderThrowable(source, ex.getCause());
    }
  }

  @Override
  public GuavaTemplateCache setReload(final boolean reload) {
    this.reload = reload;
    return this;
  }

  /**
   * Re-throw the cause of an execution exception.
   *
   * @param source The template source. Required.
   * @param cause The cause of an execution exception.
   * @return Re-throw a cause of an execution exception.
   */
  private RuntimeException launderThrowable(final TemplateSource source, final Throwable cause) {
    if (cause instanceof RuntimeException) {
      return (RuntimeException) cause;
    } else if (cause instanceof Error) {
      throw (Error) cause;
    } else {
      return new HandlebarsException("Can't parse: " + source, cause);
    }
  }

  /**
   * @param source Seed.
   * @return A template source key.
   */
  private TemplateSource key(final TemplateSource source) {
    return reload ? new ReloadableTemplateSource(source) : source;
  }
}
