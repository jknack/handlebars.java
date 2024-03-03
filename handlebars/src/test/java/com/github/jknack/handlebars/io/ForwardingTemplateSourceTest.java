/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

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
