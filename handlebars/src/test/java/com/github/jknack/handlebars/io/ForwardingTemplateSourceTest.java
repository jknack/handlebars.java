package com.github.jknack.handlebars.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class ForwardingTemplateSourceTest {

  @Test
  public void content() throws IOException {
    TemplateSource source = mock(TemplateSource.class);
    when(source.content(StandardCharsets.UTF_8)).thenReturn("abc");

    assertEquals("abc", new ForwardingTemplateSource(source).content(StandardCharsets.UTF_8));

    verify(source).content(StandardCharsets.UTF_8);
  }

  @Test
  public void filename() throws IOException {
    String filename = "filename";

    TemplateSource source = mock(TemplateSource.class);
    when(source.filename()).thenReturn(filename);

    assertEquals("filename", new ForwardingTemplateSource(source).filename());

    verify(source).filename();
  }

  @Test
  public void lastModified() throws IOException {
    long lastModified = 716L;

    TemplateSource source = mock(TemplateSource.class);
    when(source.lastModified()).thenReturn(lastModified);

    assertEquals(lastModified, new ForwardingTemplateSource(source).lastModified());

    verify(source).lastModified();
  }
}
