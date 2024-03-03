/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

/**
 * Creates a new Handlebars parser.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
public interface ParserFactory {

  /**
   * Creates a new {@link Parser}.
   *
   * @param handlebars The parser owner.
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   * @return A new {@link Parser}.
   */
  Parser create(Handlebars handlebars, String startDelimiter, String endDelimiter);
}
