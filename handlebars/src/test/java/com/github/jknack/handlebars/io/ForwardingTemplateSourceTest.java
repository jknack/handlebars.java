package com.github.jknack.handlebars.io;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class ForwardingTemplateSourceTest {

  @Test
  public void content() throws IOException {
    TemplateSource source = createMock(TemplateSource.class);
    expect(source.content()).andReturn("abc");

    replay(source);

    assertEquals("abc", new ForwardingTemplateSource(source).content());

    verify(source);
  }

  @Test
  public void filename() throws IOException {
    String filename = "filename";

    TemplateSource source = createMock(TemplateSource.class);
    expect(source.filename()).andReturn(filename);

    replay(source);

    assertEquals("filename", new ForwardingTemplateSource(source).filename());

    verify(source);
  }

  @Test
  public void lastModified() throws IOException {
    long lastModified = 716L;

    TemplateSource source = createMock(TemplateSource.class);
    expect(source.lastModified()).andReturn(lastModified);

    replay(source);

    assertEquals(lastModified, new ForwardingTemplateSource(source).lastModified());

    verify(source);
  }
}
