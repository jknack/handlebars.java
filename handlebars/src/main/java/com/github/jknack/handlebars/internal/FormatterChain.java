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
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

import com.github.jknack.handlebars.Formatter;

/**
 * Default implementation for formatter chain.
 *
 * @author edgar
 * @since 2.1.0
 */
public class FormatterChain implements Formatter.Chain {

  /** List of available formatters. */
  private List<Formatter> chain;

  /** Index of the current formatter. */
  private int index;

  /**
   * Creates a new {@link FormatterChain}.
   *
   * @param formatter List of available formatters.
   */
  public FormatterChain(final List<Formatter> formatter) {
    this.chain = formatter;
  }

  /**
   * Creates a new {@link FormatterChain}.
   *
   * @param formatter List of available formatters.
   * @param index Index of the current formatter.
   */
  private FormatterChain(final List<Formatter> formatter, final int index) {
    this.chain = formatter;
    this.index = index;
  }

  /**
   * Gets the next formatter in the chain.
   * @return The formatter at the next index.
   */
  private Formatter.Chain next() {
    if (index + 1 < chain.size()) {
      return new FormatterChain(chain, index + 1);
    } else {
      return Formatter.NOOP;
    }
  }

  @Override
  public Object format(final Object value) {
    Object output;
    Formatter formatter = chain.get(index);
    if (formatter != null) {
      output = formatter.format(value, next());
      notNull(output, "Formatter " + formatter.getClass() + " returned a null result for " + value);
    } else {
      output = value.toString();
    }
    return output;
  }

}
