/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * A template source which forwards all its method calls to another template source..
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public class ForwardingTemplateSource extends AbstractTemplateSource {

  /** The template source. */
  private final TemplateSource source;

  /**
   * Creates a new {@link ForwardingTemplateSource}.
   *
   * @param source The template source to forwards all the method calls.
   */
  public ForwardingTemplateSource(final TemplateSource source) {
    this.source = notNull(source, "The source is required.");
  }

  @Override
  public String content(final Charset charset) throws IOException {
    return source.content(charset);
  }

  @Override
  public String filename() {
    return source.filename();
  }

  @Override
  public long lastModified() {
    return source.lastModified();
  }
}
