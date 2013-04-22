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
import com.github.jknack.handlebars.io.TemplateSource;

public class NullTemplateCacheTest {

  @Test
  public void clear() {
    NullTemplateCache.INSTANCE.clear();
  }

  @Test
  public void evict() {
    TemplateSource source = createMock(TemplateSource.class);

    replay(source);

    NullTemplateCache.INSTANCE.evict(source);

    verify(source);
  }

  @Test
  public void get() throws IOException {
    TemplateSource source = createMock(TemplateSource.class);

    Template template = createMock(Template.class);

    Parser parser = createMock(Parser.class);
    expect(parser.parse(source)).andReturn(template);

    replay(source, parser, template);

    Template result = NullTemplateCache.INSTANCE.get(source, parser);
    assertEquals(template, result);

    verify(source, parser, template);
  }
}
