/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.Validate.isTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Combine two or more {@link TemplateLoader} as a single {@link TemplateLoader}. {@link
 * TemplateLoader}s are executed in the order they are provided.
 *
 * <p>Execution is as follows:
 *
 * <ul>
 *   <li>If a {@link TemplateLoader} is able to resolve a {@link TemplateSource}, that {@link
 *       TemplateSource} is considered the response.
 *   <li>If a {@link TemplateLoader} throws a {@link IOException} exception the next {@link
 *       TemplateLoader} in the chain will be used.
 * </ul>
 *
 * @author edgar.espina
 * @since 1.0.0
 */
public class CompositeTemplateLoader implements TemplateLoader {

  /** The logging system. */
  private static final Logger logger = LoggerFactory.getLogger(CompositeTemplateLoader.class);

  /** The template loader list. */
  private final TemplateLoader[] delegates;

  /**
   * Creates a new {@link CompositeTemplateLoader}.
   *
   * @param loaders The template loader chain. At least two loaders must be provided.
   */
  public CompositeTemplateLoader(final TemplateLoader... loaders) {
    isTrue(loaders.length > 1, "At least two loaders are required.");
    this.delegates = loaders;
  }

  @Override
  public TemplateSource sourceAt(final String location) throws IOException {
    for (TemplateLoader delegate : delegates) {
      try {
        return delegate.sourceAt(location);
      } catch (IOException ex) {
        // try next loader in the chain.
        logger.trace("Unable to resolve: {}, trying next loader in the chain.", location);
      }
    }
    throw new FileNotFoundException(location);
  }

  @Override
  public String resolve(final String location) {
    for (TemplateLoader delegate : delegates) {
      try {
        delegate.sourceAt(location);
        return delegate.resolve(location);
      } catch (IOException ex) {
        // try next loader in the chain.
        logger.trace("Unable to resolve: {}, trying next loader in the chain.", location);
      }
    }
    throw new IllegalStateException("Can't resolve: '" + location + "'");
  }

  @Override
  public String getPrefix() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getSuffix() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setPrefix(final String prefix) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setSuffix(final String suffix) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCharset(final Charset charset) {
    for (TemplateLoader delegate : delegates) {
      delegate.setCharset(charset);
    }
  }

  @Override
  public Charset getCharset() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the delegates template loaders.
   *
   * @return The delegates template loaders.
   */
  public Iterable<TemplateLoader> getDelegates() {
    return Arrays.asList(delegates);
  }
}
