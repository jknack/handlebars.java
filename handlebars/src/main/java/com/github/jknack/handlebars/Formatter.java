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
package com.github.jknack.handlebars;

/**
 * <p>
 * Format a variable to something else. Useful for date/long conversion. etc.. A formatter is
 * applied on simple mustache/handlebars expression, like: {{var}}, but not in block expression.
 * </p>
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 *
 * Handlebars hbs = new Handlebars();
 *
 * hbs.with(new Formatter() {
 *   public Object format(Object value, Chain next) {
 *    if (value instanceof Date) {
 *      return ((Date) value).getTime();
 *    }
 *    return next.format(value);
 *   }
 * });
 *
 * </pre>
 *
 *
 * @author edgar
 * @since 2.1.0
 */
public interface Formatter {

  /**
   * Call the next formatter in the chain.
   *
   * @author edgar
   * @since 2.1.0
   */
  interface Chain {

    /**
     * Ask the next formatter to process the value.
     *
     * @param value A value to format, not null.
     * @return A formatted value, not null.
     */
    Object format(Object value);
  }

  /**
   * NOOP Formatter.
   *
   * @author edgar
   */
  Formatter.Chain NOOP = new Formatter.Chain() {
    @Override
    public Object format(final Object value) {
      return value;
    }
  };

  /**
   * Format a value if possible or call next formatter in the chain.
   *
   * @param value A value to format, or pass it to the next formatter in the chain.
   * @param next Point to the next formatter in the chain.
   * @return A formatted value, not null.
   */
  Object format(Object value, Formatter.Chain next);
}
