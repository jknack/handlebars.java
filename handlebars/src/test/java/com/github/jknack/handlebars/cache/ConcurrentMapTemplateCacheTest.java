package com.github.jknack.handlebars.cache;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ForwardingTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateSource;

public class ConcurrentMapTemplateCacheTest {

  @Test
  public void defaultConstructor() throws IOException {
    new ConcurrentMapTemplateCache();
  }

  @Test(expected = NullPointerException.class)
  public void creationWithNullCacheMustFail() throws IOException {
    new ConcurrentMapTemplateCache(null);
  }

  @Test
  public void get() throws IOException {
    ConcurrentMap<TemplateSource, Pair<TemplateSource, Template>> cache = new ConcurrentHashMap<>();

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Template template = mock(Template.class);

    Parser parser = mock(Parser.class);
    when(parser.parse(source)).thenReturn(template);

    // 1st call, parse must be call it
    assertEquals(template, new ConcurrentMapTemplateCache(cache).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template, new ConcurrentMapTemplateCache(cache).get(source, parser));

    verify(parser).parse(source);
  }

  @Test
  public void getAndReload() throws IOException, InterruptedException {
    ConcurrentMap<TemplateSource, Pair<TemplateSource, Template>> cache = new ConcurrentHashMap<>();

    TemplateSource source = source("/template.hbs");

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
        new ConcurrentMapTemplateCache(cache).setReload(true).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template,
        new ConcurrentMapTemplateCache(cache).setReload(true).get(source, parser));

    // 3th call, parse must be call it
    assertEquals(reloadTemplate, new ConcurrentMapTemplateCache(cache).setReload(true)
        .get(reloadSource, parser));

    verify(parser).parse(same(source));
    verify(parser).parse(same(reloadSource));
  }

  @Test
  public void evict() throws IOException {
    TemplateSource source = mock(TemplateSource.class);

    @SuppressWarnings("unchecked")
    ConcurrentMap<TemplateSource, Pair<TemplateSource, Template>> cache = mock(
        ConcurrentMap.class);
    when(cache.remove(source)).thenReturn(null);

    new ConcurrentMapTemplateCache(cache).evict(source);

    verify(cache).remove(source);
  }

  @Test
  public void clear() throws IOException {
    @SuppressWarnings("unchecked")
    ConcurrentMap<TemplateSource, Pair<TemplateSource, Template>> cache = mock(
        ConcurrentMap.class);
    cache.clear();

    new ConcurrentMapTemplateCache(cache).clear();
  }

  private TemplateSource source(final String filename) throws IOException {
    TemplateSource source = new URLTemplateSource(filename, getClass().getResource(
        filename));
    return source;
  }
}
