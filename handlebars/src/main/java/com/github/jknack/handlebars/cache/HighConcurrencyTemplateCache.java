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
package com.github.jknack.handlebars.cache;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * A high concurrency template cache.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public class HighConcurrencyTemplateCache implements TemplateCache {

  /**
   * The logging system.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * The map cache.
   */
  private final ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache;

  /** Turn on/off auto reloading of templates. */
  private boolean reload;

  /**
   * Creates a new HighConcurrencyTemplateCache.
   *
   * @param cache The concurrent map cache. Required.
   */
  protected HighConcurrencyTemplateCache(
      final ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache) {
    this.cache = notNull(cache, "The cache is required.");
  }

  /**
   * Creates a new HighConcurrencyTemplateCache.
   */
  public HighConcurrencyTemplateCache() {
    this(new ConcurrentHashMap<TemplateSource, Future<Pair<TemplateSource, Template>>>());
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public void evict(final TemplateSource source) {
    cache.remove(source);
  }

  @Override
  public Template get(final TemplateSource source, final Parser parser) throws IOException {
    notNull(source, "The source is required.");
    notNull(parser, "The parser is required.");

    /**
     * Don't keep duplicated entries, remove old templates if a change is detected.
     */
    return cacheGet(source, parser);
  }

  @Override
  public HighConcurrencyTemplateCache setReload(final boolean reload) {
    this.reload = reload;
    return this;
  }

  /**
   * Get/Parse a template source.
   *
   * @param source The template source.
   * @param parser The parser.
   * @return A Handlebars template.
   * @throws IOException If we can't read input.
   */
  private Template cacheGet(final TemplateSource source, final Parser parser) throws IOException {
    notNull(source, "The source is required.");
    notNull(parser, "The parser is required.");

    boolean interrupted = false;

    FutureTask<Pair<TemplateSource, Template>> futureTask = newTask(source, parser);
    try {
      while (true) {
        Future<Pair<TemplateSource, Template>> future = cache.get(source);
        try {
          if (future == null) {
            logger.debug("Loading: {}", source);
            future = putIfAbsent(source, futureTask);
          } else if (reload && source.lastModified() != future.get().getKey().lastModified()) {
            evict(source);
            logger.debug("Reloading: {}", source);
            future = putIfAbsent(source, futureTask);
          } else {
            logger.debug("Found in cache: {}", source);
          }
          Pair<TemplateSource, Template> entry = future.get();
          return entry.getValue();
        } catch (CancellationException ex) {
          cache.remove(source, future);
        } catch (InterruptedException ex) {
          // fall through and retry
          interrupted = true;
        } catch (ExecutionException ex) {
          if (future != null) {
            cache.remove(source, future);
          }
          throw launderThrowable(source, ex.getCause());
        }
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Creates a new future task for compiling the given source.
   *
   * @param source The template source.
   * @param parser The handlebars parser.
   * @return A new future task.
   */
  private FutureTask<Pair<TemplateSource, Template>> newTask(final TemplateSource source,
      final Parser parser) {
    return new FutureTask<>(
            () -> Pair.of(source, parser.parse(source)));
  }

  /**
   * Compute and put the result of the future task.
   *
   * @param source The template source.
   * @param futureTask The future task.
   * @return The resulting value.
   */
  private Future<Pair<TemplateSource, Template>> putIfAbsent(final TemplateSource source,
      final FutureTask<Pair<TemplateSource, Template>> futureTask) {
    Future<Pair<TemplateSource, Template>> future = cache.putIfAbsent(source, futureTask);
    if (future == null) {
      future = futureTask;
      futureTask.run();
    }
    return future;
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

}
