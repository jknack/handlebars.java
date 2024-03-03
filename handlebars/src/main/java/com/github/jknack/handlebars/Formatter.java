/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

/**
 * Format a variable to something else. Useful for date/long conversion. etc.. A formatter is
 * applied on simple mustache/handlebars expression, like: {{var}}, but not in block expression.
 *
 * <p>Usage:
 *
 * <pre>
 *
 * Handlebars hbs = new Handlebars();
 *
 * hbs.with(new Formatter() {
 * 	public Object format(Object value, Chain next) {
 * 		if (value instanceof Date) {
 * 			return ((Date) value).getTime();
 * 		}
 * 		return next.format(value);
 * 	}
 * });
 *
 * </pre>
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
  Formatter.Chain NOOP = value -> value;

  /**
   * Format a value if possible or call next formatter in the chain.
   *
   * @param value A value to format, or pass it to the next formatter in the chain.
   * @param next Point to the next formatter in the chain.
   * @return A formatted value, not null.
   */
  Object format(Object value, Formatter.Chain next);
}
