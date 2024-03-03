/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;

import com.github.jknack.handlebars.Context;

/**
 * String or char literals.
 *
 * @author edgar
 * @since 4.0.3
 */
public class StrParam implements Param {

  /** Literal . */
  private final String literal;

  /** Value. */
  private String value;

  /**
   * Creates a new {@link StrParam}.
   *
   * @param literal Value.
   */
  public StrParam(final String literal) {
    this.literal = literal;
    this.value = literal.substring(1, literal.length() - 1);
  }

  @Override
  public Object apply(final Context context) throws IOException {
    return value;
  }

  @Override
  public String toString() {
    return literal;
  }
}
