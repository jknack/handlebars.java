package com.github.jknack.handlebars;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

public class Issue266 {

  @Test
  public void prettyPrint() {
    Handlebars handlebars = new Handlebars().prettyPrint(false);
    assertNotNull(handlebars);
  }

  @Test
  public void stringParams() {
    Handlebars handlebars = new Handlebars().stringParams(false);
    assertNotNull(handlebars);
  }

  @Test
  public void infiniteLoops() {
    Handlebars handlebars = new Handlebars().infiniteLoops(false);
    assertNotNull(handlebars);
  }

  @Test
  public void endDelimiter() {
    Handlebars handlebars = new Handlebars().endDelimiter(">>");
    assertNotNull(handlebars);
  }

  @Test
  public void startDelimiter() {
    Handlebars handlebars = new Handlebars().startDelimiter("<<");
    assertNotNull(handlebars);
  }

  @Test
  public void withTemplateLoader() {
    Handlebars handlebars = new Handlebars().with(new ClassPathTemplateLoader());
    assertNotNull(handlebars);
  }

  @Test
  public void withParserFactory() {
    ParserFactory parserFactory = createMock(ParserFactory.class);

    replay(parserFactory);

    Handlebars handlebars = new Handlebars().with(parserFactory);
    assertNotNull(handlebars);

    verify(parserFactory);
  }

  @Test
  public void withTemplateCache() {
    TemplateCache cache = createMock(TemplateCache.class);

    replay(cache);

    Handlebars handlebars = new Handlebars().with(cache);
    assertNotNull(handlebars);

    verify(cache);
  }

  @Test
  public void withHelperRegistry() {
    HelperRegistry registry = createMock(HelperRegistry.class);

    replay(registry);

    Handlebars handlebars = new Handlebars().with(registry);
    assertNotNull(handlebars);

    verify(registry);
  }

  @Test
  public void withEscapingStrategy() {
    EscapingStrategy escapingStrategy = createMock(EscapingStrategy.class);

    replay(escapingStrategy);

    Handlebars handlebars = new Handlebars().with(escapingStrategy);
    assertNotNull(handlebars);

    verify(escapingStrategy);
  }
}
