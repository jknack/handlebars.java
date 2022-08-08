/**
 * Copyright (c) 2012-2015 Edgar Espina
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.jknack.handlebars.Handlebars;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Decorates an existing TemplateLoader with a GuavaCache.
 * This is useful to avoid constantly creating TemplateSources.
 * @author agent
 */
public class GuavaCachedTemplateLoader implements TemplateLoader {

  /**
   * never null.
   */
  private final TemplateLoader delegate;
  /**
   * never null.
   */
  private final Cache<String, TemplateSource> cache;

  /**
   * @param delegate
   *          wrappped template loader.
   * @param cache
   *          Guava Cache.
   */
  public GuavaCachedTemplateLoader(final TemplateLoader delegate,
      final Cache<String, TemplateSource> cache) {
    super();
    this.delegate = delegate;
    this.cache = cache;
  }

  /**
   * Create a cached template loader that will expire entries if they are not
   * used after some time.
   * @param delegate
   *          to be decorated.
   * @param duration
   *          never negative.
   * @param unit
   *          never null.
   * @return never null.
   */
  public static GuavaCachedTemplateLoader cacheWithExpiration(
      final TemplateLoader delegate, final long duration, final TimeUnit unit) {
    Cache<String, TemplateSource> cache = CacheBuilder.newBuilder()
        .expireAfterAccess(duration, unit).build();
    return new GuavaCachedTemplateLoader(delegate, cache);
  }

  /**
   * {@inheritDoc}
   */
  public TemplateSource sourceAt(final String location) throws IOException {
    try {
      return cache.get(location, () -> delegate.sourceAt(location));
    } catch (ExecutionException e) {
      throw Handlebars.Utils.propagate(e.getCause());
    }
  }

  /**
   * {@inheritDoc}
   */
  public String resolve(final String location) {
    return delegate.resolve(location);
  }

  /**
   * {@inheritDoc}
   */
  public String getPrefix() {
    return delegate.getPrefix();
  }

  /**
   * {@inheritDoc}
   */
  public String getSuffix() {
    return delegate.getSuffix();
  }

  /**
   * {@inheritDoc}
   */
  public void setPrefix(final String prefix) {
    delegate.setPrefix(prefix);
  }

  /**
   * {@inheritDoc}
   */
  public void setSuffix(final String suffix) {
    delegate.setSuffix(suffix);
  }

  @Override public void setCharset(final Charset charset) {
   delegate.setCharset(charset);
  }

  @Override public Charset getCharset() {
    return delegate.getCharset();
  }
}
