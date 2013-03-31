package com.github.jknack.handlebars.cache;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

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
    Cache<TemplateSource, Template> cache = CacheBuilder.newBuilder()
        .build();

    TemplateSource source = new URLTemplateSource("/template.hbs", getClass().getResource(
        "/template.hbs"));

    Template template = createMock(Template.class);

    Parser parser = createMock(Parser.class);
    expect(parser.parse(source)).andReturn(template);

    replay(parser, template);

    // 1st call, parse must be call it
    assertEquals(template, new GuavaTemplateCache(cache).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template, new GuavaTemplateCache(cache).get(source, parser));

    verify(parser, template);
  }

  @Test
  public void getAndReload() throws IOException, InterruptedException {
    Cache<TemplateSource, Template> cache = CacheBuilder.newBuilder()
        .build();

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
    assertEquals(template, new GuavaTemplateCache(cache).get(source, parser));

    // 2nd call, should return from cache
    assertEquals(template, new GuavaTemplateCache(cache).get(source, parser));

    // 3th call, parse must be call it
    assertEquals(reloadTemplate, new GuavaTemplateCache(cache).get(reloadSource, parser));

    verify(parser, template, reloadTemplate);
  }

  @Test
  public void evict() throws IOException {
    TemplateSource source = createMock(TemplateSource.class);

    @SuppressWarnings("unchecked")
    Cache<TemplateSource, Template> cache = createMock(Cache.class);
    cache.invalidate(source);

    replay(cache, source);

    new GuavaTemplateCache(cache).evict(source);

    verify(cache, source);
  }

  @Test
  public void clear() throws IOException {
    @SuppressWarnings("unchecked")
    Cache<TemplateSource, Template> cache = createMock(Cache.class);
    cache.invalidateAll();

    replay(cache);

    new GuavaTemplateCache(cache).clear();

    verify(cache);
  }

  private TemplateSource source(final String filename) throws IOException {
    TemplateSource source = new URLTemplateSource(filename, getClass().getResource(
        filename));
    return source;
  }
}
