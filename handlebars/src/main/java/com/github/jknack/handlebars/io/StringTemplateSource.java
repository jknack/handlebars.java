/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.Validate.notNull;

import java.nio.charset.Charset;

/**
 * String implementation of {@link TemplateSource}.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public class StringTemplateSource extends AbstractTemplateSource {

  /** The template's content. Required. */
  private final String content;

  /** The template's file name. Required. */
  private final String filename;

  /** The last modified date. */
  private final long lastModified;

  /**
   * Creates a new {@link StringTemplateSource}.
   *
   * @param filename The template's file name. Required.
   * @param content The template's content. Required.
   */
  public StringTemplateSource(final String filename, final String content) {
    this.content = notNull(content, "The content is required.");
    this.filename = notNull(filename, "The filename is required.");
    this.lastModified = content.hashCode();
  }

  @Override
  public String content(final Charset charset) {
    return content;
  }

  @Override
  public String filename() {
    return filename;
  }

  @Override
  public long lastModified() {
    return lastModified;
  }
}
