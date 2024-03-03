/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ForwardingTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateSource;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class GuavaTemplateCacheTest {

  @Test
  public void get() throws IOException {
    Cache<TemplateSource, Template> cache = CacheBuilder.newBuilder().build();

    TemplateSource source =
        new URLTemplateSource("/template.hbs", getClass().getResource("/template.hbs"));

    Template template = mock(Template.class);

    Parser parser = mock(Parser.class);
    when(parser.parse(source)).thenReturn(template);

    // 1st call, parse must be call it
    assertEquals(template, new GuavaTemplateCache(cache).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template, new GuavaTemplateCache(cache).get(source, parser));

    verify(parser).parse(source);
  }

  @Test
  public void getAndReload() throws IOException, InterruptedException {
    Cache<TemplateSource, Template> cache = CacheBuilder.newBuilder().build();

    TemplateSource source = source("/template.hbs");

    Template template = mock(Template.class);

    Template reloadTemplate = mock(Template.class);

    Parser parser = mock(Parser.class);
    when(parser.parse(same(source))).thenReturn(template);

    TemplateSource reloadSource =
        new ForwardingTemplateSource(source) {
          @Override
          public long lastModified() {
            return System.currentTimeMillis() * 7;
          }
        };
    when(parser.parse(same(reloadSource))).thenReturn(reloadTemplate);

    // 1st call, parse must be call it
    assertEquals(template, new GuavaTemplateCache(cache).setReload(true).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template, new GuavaTemplateCache(cache).setReload(true).get(source, parser));

    // 3th call, parse must be call it
    assertEquals(
        reloadTemplate, new GuavaTemplateCache(cache).setReload(true).get(reloadSource, parser));

    verify(parser).parse(same(source));
    verify(parser).parse(same(reloadSource));
  }

  @Test
  public void evict() throws IOException {
    TemplateSource source = mock(TemplateSource.class);

    @SuppressWarnings("unchecked")
    Cache<TemplateSource, Template> cache = mock(Cache.class);
    cache.invalidate(source);

    new GuavaTemplateCache(cache).evict(source);
  }

  @Test
  public void clear() throws IOException {
    @SuppressWarnings("unchecked")
    Cache<TemplateSource, Template> cache = mock(Cache.class);
    cache.invalidateAll();

    new GuavaTemplateCache(cache).clear();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void executionExceptionWithRuntimeException() throws IOException, ExecutionException {
    Assertions.assertThrows(
        IllegalStateException.class,
        () -> {
          TemplateSource source = mock(TemplateSource.class);

          Parser parser = mock(Parser.class);

          Cache<TemplateSource, Template> cache = mock(Cache.class);
          when(cache.get(eq(source), any(Callable.class)))
              .thenThrow(new ExecutionException(new IllegalStateException()));

          new GuavaTemplateCache(cache).get(source, parser);

          verify(cache).get(eq(source), any(Callable.class));
        });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void executionExceptionWithError() throws IOException, ExecutionException {
    Assertions.assertThrows(
        Error.class,
        () -> {
          TemplateSource source = mock(TemplateSource.class);

          Parser parser = mock(Parser.class);

          Cache<TemplateSource, Template> cache = mock(Cache.class);
          when(cache.get(eq(source), any(Callable.class)))
              .thenThrow(new ExecutionException(new Error()));

          new GuavaTemplateCache(cache).get(source, parser);

          verify(cache).get(eq(source), any(Callable.class));
        });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void executionExceptionWithCheckedException() throws IOException, ExecutionException {
    Assertions.assertThrows(
        HandlebarsException.class,
        () -> {
          TemplateSource source = mock(TemplateSource.class);

          Parser parser = mock(Parser.class);

          Cache<TemplateSource, Template> cache = mock(Cache.class);
          when(cache.get(eq(source), any(Callable.class)))
              .thenThrow(new ExecutionException(new IOException()));

          new GuavaTemplateCache(cache).get(source, parser);

          verify(cache).get(eq(source), any(Callable.class));
        });
  }

  private TemplateSource source(final String filename) throws IOException {
    TemplateSource source = new URLTemplateSource(filename, getClass().getResource(filename));
    return source;
  }
}
