/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;

import com.github.jknack.handlebars.Context;

/**
 * Int or boolean param.
 *
 * @author edgar
 * @since 4.0.3
 */
public class DefParam implements Param {

  /** Value. */
  private Object value;

  /**
   * Creates a new {@link DefParam}.
   *
   * @param value Int or boolean value.
   */
  public DefParam(final Object value) {
    this.value = value;
  }

  @Override
  public Object apply(final Context context) throws IOException {
    return this.value;
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
