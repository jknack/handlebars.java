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

import java.util.Iterator;
import java.util.List;

import com.github.jknack.handlebars.Formatter;

/**
 * Default implementation for formatter chain.
 *
 * @author edgar
 * @since 2.1.0
 */
public class FormatterChain implements Formatter.Chain {

  /** Pointer to next formatter. */
  private Iterator<Formatter> chain;

  /**
   * Creates a new {@link FormatterChain}.
   *
   * @param formatter List of available formatters.
   */
  public FormatterChain(final List<Formatter> formatter) {
    this.chain = formatter.iterator();
  }

  @Override
  public Object format(final Object value) {
    Object output;
    if (chain.hasNext()) {
      Formatter formatter = chain.next();
      output = formatter.format(value, this);
      notNull(output, "Formatter " + formatter.getClass() + " returned a null result for " + value);
    } else {
      output = value.toString();
    }
    return output;
  }

}
