package com.github.jknack.handlebars.cache;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.easymock.Capture;
import org.junit.Test;

import com.github.jknack.handlebars.HandlebarsException;
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
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = new ConcurrentHashMap<TemplateSource, Future<Pair<TemplateSource, Template>>>();

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Template template = createMock(Template.class);

    Parser parser = createMock(Parser.class);
    expect(parser.parse(source)).andReturn(template);

    replay(parser, template);

    // 1st call, parse must be call it
    assertEquals(template, new HighConcurrencyTemplateCache(cache).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template, new HighConcurrencyTemplateCache(cache).get(source, parser));

    verify(parser, template);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void interruptThreadOnInterruptedException() throws Exception {
    assertEquals(false, Thread.currentThread().isInterrupted());
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = createMock(
        ConcurrentHashMap.class);

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Future<Pair<TemplateSource, Template>> future = createMock(Future.class);
    // 1st try interrupt thread
    expect(cache.get(source)).andReturn(future);
    expect(future.get()).andThrow(new InterruptedException());

    // 2nd success
    Template template = createMock(Template.class);
    Pair<TemplateSource, Template> pair = createMock(Pair.class);
    expect(pair.getValue()).andReturn(template);
    expect(cache.get(source)).andReturn(future);
    expect(future.get()).andReturn(pair);

    Parser parser = createMock(Parser.class);

    replay(parser, template, cache, future, pair);

    // 1st call, parse must be call it
    assertEquals(template, new HighConcurrencyTemplateCache(cache).get(source, parser));
    assertEquals(true, Thread.currentThread().isInterrupted());

    verify(parser, template, cache, future, pair);
  }

  @Test(expected = Error.class)
  @SuppressWarnings("unchecked")
  public void errorShouldBeReThrow() throws Exception {
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = createMock(
        ConcurrentHashMap.class);

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Future<Pair<TemplateSource, Template>> future = createMock(Future.class);
    // 1st try interrupt thread
    expect(cache.get(source)).andReturn(future);
    expect(future.get()).andThrow(new Error());

    // 2nd success
    Template template = createMock(Template.class);
    Pair<TemplateSource, Template> pair = createMock(Pair.class);
    expect(pair.getLeft()).andReturn(source);
    expect(pair.getValue()).andReturn(template);
    expect(cache.get(source)).andReturn(future);
    expect(future.get()).andReturn(pair).times(2);

    Parser parser = createMock(Parser.class);

    replay(parser, template, cache, future, pair);

    // 1st call, parse must be call it
    assertEquals(template, new HighConcurrencyTemplateCache(cache).get(source, parser));

    verify(parser, template, cache, future, pair);
  }

  @Test
  public void getAndReload() throws IOException, InterruptedException {
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = new ConcurrentHashMap<TemplateSource, Future<Pair<TemplateSource, Template>>>();

    TemplateSource source = source("/template.hbs");

    Template template = createMock(Template.class);

    Template reloadTemplate = createMock(Template.class);

    Parser parser = createMock(Parser.class);
    expect(parser.parse(source)).andReturn(template);

    TemplateSource reloadSource = new ForwardingTemplateSource(source) {
      @Override
      public long lastModified() {
        return System.currentTimeMillis() * 7;
      }
    };
    expect(parser.parse(reloadSource)).andReturn(reloadTemplate);

    replay(parser, template, reloadTemplate);

    // 1st call, parse must be call it
    assertEquals(template,
        new HighConcurrencyTemplateCache(cache).setReload(true).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template,
        new HighConcurrencyTemplateCache(cache).setReload(true).get(source, parser));

    // 3th call, parse must be call it
    assertEquals(reloadTemplate,
        new HighConcurrencyTemplateCache(cache).setReload(true).get(reloadSource, parser));

    verify(parser, template, reloadTemplate);
  }

  @Test
  public void evict() throws IOException {
    TemplateSource source = createMock(TemplateSource.class);

    @SuppressWarnings("unchecked")
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = createMock(
        ConcurrentMap.class);
    expect(cache.remove(source)).andReturn(null);

    replay(cache, source);

    new HighConcurrencyTemplateCache(cache).evict(source);

    verify(cache, source);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void cancellationException() throws IOException, InterruptedException, ExecutionException {
    TemplateSource source = createMock(TemplateSource.class);

    Template template = createMock(Template.class);

    Future<Pair<TemplateSource, Template>> future = createMock(Future.class);
    expect(future.get()).andThrow(new CancellationException());
    expect(future.get()).andReturn(ImmutablePair.<TemplateSource, Template> of(source, template));

    Capture<TemplateSource> keyGet = new Capture<TemplateSource>();
    Capture<TemplateSource> keyRemove = new Capture<TemplateSource>();

    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = createMock(
        ConcurrentMap.class);
    expect(cache.get(capture(keyGet))).andReturn(future).times(2);
    expect(cache.remove(capture(keyRemove), eq(future))).andReturn(true);

    Parser parser = createMock(Parser.class);

    Object[] mocks = {cache, source, future, template };

    replay(mocks);

    Template result = new HighConcurrencyTemplateCache(cache).get(source, parser);
    assertEquals(template, result);

    verify(mocks);
  }

  @Test(expected = IllegalArgumentException.class)
  @SuppressWarnings("unchecked")
  public void runtimeException() throws IOException, InterruptedException, ExecutionException {
    TemplateSource source = createMock(TemplateSource.class);
    expect(source.lastModified()).andReturn(615L).times(3);

    Template template = createMock(Template.class);

    Future<Pair<TemplateSource, Template>> future = createMock(Future.class);
    expect(future.get()).andThrow(new ExecutionException(new IllegalArgumentException()));

    Capture<TemplateSource> keyGet = new Capture<TemplateSource>();
    Capture<TemplateSource> keyRemove = new Capture<TemplateSource>();

    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = createMock(
        ConcurrentMap.class);

    expect(cache.get(capture(keyGet))).andReturn(future);
    expect(cache.remove(capture(keyRemove), eq(future))).andReturn(true);

    Parser parser = createMock(Parser.class);

    Object[] mocks = {cache, source, future, template };

    replay(mocks);

    Template result = new HighConcurrencyTemplateCache(cache).get(source, parser);
    assertEquals(template, result);

    verify(mocks);
  }

  @Test(expected = Error.class)
  @SuppressWarnings("unchecked")
  public void errorException() throws IOException, InterruptedException, ExecutionException {
    TemplateSource source = createMock(TemplateSource.class);
    expect(source.lastModified()).andReturn(615L).times(3);

    Template template = createMock(Template.class);

    Future<Pair<TemplateSource, Template>> future = createMock(Future.class);
    expect(future.get()).andThrow(new ExecutionException(new Error()));

    Capture<TemplateSource> keyGet = new Capture<TemplateSource>();

    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = createMock(
        ConcurrentMap.class);
    expect(cache.get(capture(keyGet))).andReturn(future);

    Parser parser = createMock(Parser.class);

    Object[] mocks = {cache, source, future, template };

    replay(mocks);

    Template result = new HighConcurrencyTemplateCache(cache).get(source, parser);
    assertEquals(template, result);

    verify(mocks);
  }

  @Test(expected = HandlebarsException.class)
  @SuppressWarnings("unchecked")
  public void hbsException() throws IOException, InterruptedException, ExecutionException {
    TemplateSource source = createMock(TemplateSource.class);
    expect(source.filename()).andReturn("filename");
    expect(source.lastModified()).andReturn(615L);

    Template template = createMock(Template.class);

    Future<Pair<TemplateSource, Template>> future = createMock(Future.class);
    expect(future.get()).andThrow(new ExecutionException(new Exception()));

    Capture<TemplateSource> keyGet = new Capture<TemplateSource>();
    Capture<TemplateSource> keyRemove = new Capture<TemplateSource>();

    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = createMock(
        ConcurrentMap.class);
    expect(cache.get(capture(keyGet))).andReturn(future);
    expect(cache.remove(capture(keyRemove), eq(future))).andReturn(true);

    Parser parser = createMock(Parser.class);

    Object[] mocks = {cache, source, future, template };

    replay(mocks);

    Template result = new HighConcurrencyTemplateCache(cache).get(source, parser);
    assertEquals(template, result);

    verify(mocks);
  }

  @Test
  public void clear() throws IOException {
    @SuppressWarnings("unchecked")
    ConcurrentMap<TemplateSource, Future<Pair<TemplateSource, Template>>> cache = createMock(
        ConcurrentMap.class);
    cache.clear();

    replay(cache);

    new HighConcurrencyTemplateCache(cache).clear();

    verify(cache);
  }

  private TemplateSource source(final String filename) throws IOException {
    TemplateSource source = new URLTemplateSource(filename, getClass().getResource(
        filename));
    return source;
  }
}
