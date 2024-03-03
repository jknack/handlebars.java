/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.util.List;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.PathExpression;

/**
 * Look param in the given context.
 *
 * @author edgar
 * @since 4.0.3
 */
public class RefParam implements Param {

  /** Compiled expression . */
  private final List<PathExpression> value;

  /**
   * Creates a new {@link RefParam}.
   *
   * @param value A compiled expression.
   */
  public RefParam(final List<PathExpression> value) {
    this.value = value;
  }

  @Override
  public Object apply(final Context context) {
    return context.get(this.value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
