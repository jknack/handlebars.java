package com.github.jknack.handlebars;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

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
    ParserFactory parserFactory = mock(ParserFactory.class);

    Handlebars handlebars = new Handlebars().with(parserFactory);
    assertNotNull(handlebars);
  }

  @Test
  public void withTemplateCache() {
    TemplateCache cache = mock(TemplateCache.class);

    Handlebars handlebars = new Handlebars().with(cache);
    assertNotNull(handlebars);
  }

  @Test
  public void withHelperRegistry() {
    HelperRegistry registry = mock(HelperRegistry.class);

    Handlebars handlebars = new Handlebars().with(registry);
    assertNotNull(handlebars);
  }

  @Test
  public void withEscapingStrategy() {
    EscapingStrategy escapingStrategy = mock(EscapingStrategy.class);

    Handlebars handlebars = new Handlebars().with(escapingStrategy);
    assertNotNull(handlebars);
  }
}
