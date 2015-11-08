/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.Validate.isTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Combine two or more {@link TemplateLoader} as a single {@link TemplateLoader}.
 * {@link TemplateLoader}s are executed in the order they are provided.
 * </p>
 * <p>
 * Execution is as follows:
 * </p>
 * <ul>
 * <li>If a {@link TemplateLoader} is able to resolve a {@link TemplateSource}, that
 * {@link TemplateSource} is considered the response.</li>
 * <li>If a {@link TemplateLoader} throws a {@link IOException} exception the next
 * {@link TemplateLoader} in the chain will be used.</li>
 * </ul>
 *
 * @author edgar.espina
 * @since 1.0.0
 */
public class CompositeTemplateLoader implements TemplateLoader {

  /**
   * The logging system.
   */
  private static final Logger logger = LoggerFactory.getLogger(CompositeTemplateLoader.class);

  /**
   * The template loader list.
   */
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

  /**
   * Returns the delegates template loaders.
   *
   * @return The delegates template loaders.
   */
  public Iterable<TemplateLoader> getDelegates() {
    return Arrays.asList(delegates);
  }
}
