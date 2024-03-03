/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.cache;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Caffeine template cache for handlebars.
 *
 * @author edgar
 * @since 4.3.0
 */
public class CaffeineTemplateCache implements TemplateCache {

  /** Internal cache. */
  private final Cache<TemplateSource, Template> cache;

  /**
   * Creates a new template cache.
   *
   * @param cache Cache.
   */
  public CaffeineTemplateCache(final Cache<TemplateSource, Template> cache) {
    this.cache = requireNonNull(cache, "The cache is required.");
  }

  @Override
  public void clear() {
    cache.invalidateAll();
  }

  @Override
  public void evict(final TemplateSource source) {
    cache.invalidate(source);
  }

  @Override
  public Template get(final TemplateSource source, final Parser parser) {
    return cache.get(source, parseTemplate(parser));
  }

  /**
   * This method does nothing on Caffeine. Better option is to use a loading cache with a eviction
   * policy of your choice.
   *
   * <p>Don't use this method.
   *
   * @param reload Ignored.
   * @return This template cache.
   */
  @Override
  public TemplateCache setReload(final boolean reload) {
    // NOOP
    return this;
  }

  private Function<? super TemplateSource, ? extends Template> parseTemplate(final Parser parser) {
    return source -> {
      try {
        return parser.parse(source);
      } catch (IOException ex) {
        throw Handlebars.Utils.propagate(ex);
      }
    };
  }
}
