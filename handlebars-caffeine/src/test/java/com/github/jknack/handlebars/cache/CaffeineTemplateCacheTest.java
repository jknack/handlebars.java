package com.github.jknack.handlebars.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.function.Function;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

public class CaffeineTemplateCacheTest {

  @Test
  public void shouldInvalidateKeys() {
    Cache<TemplateSource, Template> cache = mock(Cache.class);

    CaffeineTemplateCache templateCache = new CaffeineTemplateCache(cache);
    templateCache.clear();

    verify(cache).invalidateAll();
  }

  @Test
  public void shouldInvalidateOneKey() {
    Cache<TemplateSource, Template> cache = mock(Cache.class);

    TemplateSource key = mock(TemplateSource.class);

    CaffeineTemplateCache templateCache = new CaffeineTemplateCache(cache);
    templateCache.evict(key);

    verify(cache).invalidate(key);
  }

  @Test
  public void shouldDoNothingOnSetReload() {
    Cache<TemplateSource, Template> cache = mock(Cache.class);

    CaffeineTemplateCache templateCache = new CaffeineTemplateCache(cache);

    // Who care?
    templateCache.setReload(true);
    templateCache.setReload(false);
  }

  @Test
  public void shouldGetTemplate() throws IOException {
    Template template = mock(Template.class);

    TemplateSource key = mock(TemplateSource.class);

    Parser parser = mock(Parser.class);
    when(parser.parse(key)).thenReturn(template);

    ArgumentCaptor<Function<TemplateSource, Template>> captor = ArgumentCaptor.forClass(
        Function.class);

    Cache<TemplateSource, Template> cache = mock(Cache.class);
    when(cache.get(eq(key), captor.capture()))
        .thenReturn(template);

    CaffeineTemplateCache templateCache = new CaffeineTemplateCache(cache);

    assertEquals(template, templateCache.get(key, parser));

    captor.getValue().apply(key);
  }

  @Test
  public void shouldReThrowExceptionOnGetTemplate() throws IOException {
    TemplateSource key = mock(TemplateSource.class);

    HandlebarsException error = new HandlebarsException("error", null);

    Parser parser = mock(Parser.class);
    when(parser.parse(key)).thenThrow(error);

    ArgumentCaptor<Function<TemplateSource, Template>> captor = ArgumentCaptor.forClass(
        Function.class);

    Cache<TemplateSource, Template> cache = Caffeine.newBuilder()
        .build();

    CaffeineTemplateCache templateCache = new CaffeineTemplateCache(cache);

    try {
      templateCache.get(key, parser);
      fail("Should get error");
    } catch (HandlebarsException x) {
      assertEquals(error, x);
    }
  }
}
