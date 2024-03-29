/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

public class NullTemplateCacheTest {

  @Test
  public void clear() {
    NullTemplateCache.INSTANCE.clear();
  }

  @Test
  public void evict() {
    TemplateSource source = mock(TemplateSource.class);

    NullTemplateCache.INSTANCE.evict(source);
  }

  @Test
  public void get() throws IOException {
    TemplateSource source = mock(TemplateSource.class);

    Template template = mock(Template.class);

    Parser parser = mock(Parser.class);
    when(parser.parse(source)).thenReturn(template);

    Template result = NullTemplateCache.INSTANCE.get(source, parser);
    assertEquals(template, result);

    verify(parser).parse(source);
  }
}
