package com.github.jknack.handlebars.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.github.jknack.handlebars.HandlebarsException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ForwardingTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateSource;

public class HighConcurrencyTemplateCacheTest {
  @Test
  public void defaultConstructor() throws IOException {
    new HighConcurrencyTemplateCache();
  }

  @Test(expected = NullPointerException.class)
  public void creationWithNullCacheMustFail() throws IOException {
    new HighConcurrencyTemplateCache(null);
  }

  @Test
  public void get() throws IOException {
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = new ConcurrentHashMap<>();

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Template template = mock(Template.class);

    Parser parser = mock(Parser.class);
    when(parser.parse(source)).thenReturn(template);

    // 1st call, parse must be call it
    assertEquals(template, new HighConcurrencyTemplateCache(cache).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template, new HighConcurrencyTemplateCache(cache).get(source, parser));

    verify(parser).parse(source);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void interruptThreadOnInterruptedException() throws Exception {
    assertFalse(Thread.currentThread().isInterrupted());
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = mock(
        ConcurrentHashMap.class);

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Future<Pair<TemplateSource, Template>> future = mock(Future.class);
    // 1st try interrupt thread
    when(cache.get(source)).thenReturn(future);
    Pair<TemplateSource, Template> pair = mock(Pair.class);
    when(future.get()).thenThrow(new InterruptedException())
            .thenReturn(pair);

    // 2nd success
    Template template = mock(Template.class);
    when(pair.getValue()).thenReturn(template);

    Parser parser = mock(Parser.class);

    // 1st call, parse must be call it
    assertEquals(template, new HighConcurrencyTemplateCache(cache).get(source, parser));
    assertTrue(Thread.currentThread().isInterrupted());

    verify(cache, times(2)).get(source);
    verify(future, times(2)).get();
    verify(pair).getValue();
  }

  @Test(expected = Error.class)
  @SuppressWarnings("unchecked")
  public void errorShouldBeReThrow() throws Exception {
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = mock(
        ConcurrentHashMap.class);

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Future<Pair<TemplateSource, Template>> future = mock(Future.class);
    // 1st try interrupt thread
    when(cache.get(source)).thenReturn(future);
    when(future.get()).thenThrow(new Error());

    // 2nd success
    Template template = mock(Template.class);
    Pair<TemplateSource, Template> pair = mock(Pair.class);
    when(pair.getLeft()).thenReturn(source);
    when(pair.getValue()).thenReturn(template);
    when(future.get()).thenReturn(pair);

    Parser parser = mock(Parser.class);

    // 1st call, parse must be call it
    assertEquals(template, new HighConcurrencyTemplateCache(cache).get(source, parser));

    verify(cache, times(2)).get(source);
    verify(future, times(3)).get();
    verify(pair).getLeft();
    verify(pair).getValue();
  }

  @Test
  public void getAndReload() throws IOException, InterruptedException {
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = new ConcurrentHashMap<>();

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Template template = mock(Template.class);

    Template reloadTemplate = mock(Template.class);

    Parser parser = mock(Parser.class);
    when(parser.parse(same(source))).thenReturn(template);

    TemplateSource reloadSource = new ForwardingTemplateSource(source) {
      @Override
      public long lastModified() {
        return System.currentTimeMillis() * 7;
      }
    };
    when(parser.parse(same(reloadSource))).thenReturn(reloadTemplate);

    // 1st call, parse must be call it
    assertEquals(template,
        new HighConcurrencyTemplateCache(cache).setReload(true).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template,
        new HighConcurrencyTemplateCache(cache).setReload(true).get(source, parser));

    // 3th call, parse must be call it
    assertEquals(reloadTemplate,
        new HighConcurrencyTemplateCache(cache).setReload(true).get(reloadSource, parser));

    verify(parser).parse(same(source));
    verify(parser).parse(same(reloadSource));
  }

  @Test
  public void evict() throws IOException {
    TemplateSource source = mock(TemplateSource.class);

    @SuppressWarnings("unchecked")
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = mock(
        ConcurrentMap.class);
    when(cache.remove(source)).thenReturn(null);

    new HighConcurrencyTemplateCache(cache).evict(source);

    verify(cache).remove(source);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void cancellationException() throws IOException, InterruptedException, ExecutionException {
    TemplateSource source = mock(TemplateSource.class);

    Template template = mock(Template.class);

    Future<Pair<TemplateSource, Template>> future = mock(Future.class);
    when(future.get()).thenThrow(new CancellationException())
        .thenReturn(ImmutablePair.of(source, template));

    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = mock(
        ConcurrentMap.class);
    when(cache.get(any(TemplateSource.class))).thenReturn(future);
    when(cache.remove(any(TemplateSource.class), eq(future))).thenReturn(true);

    Parser parser = mock(Parser.class);

    Template result = new HighConcurrencyTemplateCache(cache).get(source, parser);
    assertEquals(template, result);

    verify(future, times(2)).get();
    verify(cache, times(2)).get(any(TemplateSource.class));
    verify(cache).remove(any(TemplateSource.class), eq(future));
  }

  @Test(expected = IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void runtimeException() throws IOException, InterruptedException, ExecutionException {
    TemplateSource source = mock(TemplateSource.class);
    when(source.lastModified()).thenReturn(615L);

    Template template = mock(Template.class);

    Future<Pair<TemplateSource, Template>> future = mock(Future.class);
    when(future.get()).thenThrow(new ExecutionException(new IllegalArgumentException()));

    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = mock(
        ConcurrentMap.class);
    when(cache.get(any(TemplateSource.class))).thenReturn(future);
    when(cache.remove(any(TemplateSource.class), eq(future))).thenReturn(true);

    Parser parser = mock(Parser.class);

    Template result = new HighConcurrencyTemplateCache(cache).get(source, parser);
    assertEquals(template, result);

    verify(source, times(3)).lastModified();
    verify(future).get();
    verify(cache).get(any(TemplateSource.class));
    verify(cache).remove(any(TemplateSource.class), eq(future));
  }

  @Test(expected = Error.class)
  @SuppressWarnings("unchecked")
  public void errorException() throws IOException, InterruptedException, ExecutionException {
    TemplateSource source = mock(TemplateSource.class);
    when(source.lastModified()).thenReturn(615L);

    Template template = mock(Template.class);

    Future<Pair<TemplateSource, Template>> future = mock(Future.class);
    when(future.get()).thenThrow(new ExecutionException(new Error()));

    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = mock(
        ConcurrentMap.class);
    when(cache.get(any(TemplateSource.class))).thenReturn(future);

    Parser parser = mock(Parser.class);

    Template result = new HighConcurrencyTemplateCache(cache).get(source, parser);
    assertEquals(template, result);

    verify(source, times(3)).lastModified();
    verify(future).get();
    verify(cache).get(any(TemplateSource.class));
  }

  @Test(expected = HandlebarsException.class)
  @SuppressWarnings("unchecked")
  public void hbsException() throws IOException, InterruptedException, ExecutionException {
    TemplateSource source = mock(TemplateSource.class);
    when(source.filename()).thenReturn("filename");
    when(source.lastModified()).thenReturn(615L);

    Template template = mock(Template.class);

    Future<Pair<TemplateSource, Template>> future = mock(Future.class);
    when(future.get()).thenThrow(new ExecutionException(new Exception()));

    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = mock(
        ConcurrentMap.class);
    when(cache.get(any(TemplateSource.class))).thenReturn(future);
    when(cache.remove(any(TemplateSource.class), eq(future))).thenReturn(true);

    Parser parser = mock(Parser.class);

    Template result = new HighConcurrencyTemplateCache(cache).get(source, parser);
    assertEquals(template, result);

    verify(source).filename();
    verify(source).lastModified();
    verify(future).get();
    verify(cache).get(any(TemplateSource.class));
    verify(cache).remove(any(TemplateSource.class), eq(future));
  }

  @Test
  public void clear() throws IOException {
    @SuppressWarnings("unchecked")
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = mock(
        ConcurrentMap.class);
    cache.clear();

    new HighConcurrencyTemplateCache(cache).clear();
  }
}
