/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;

import com.github.jknack.handlebars.Context;

/**
 * Var sub-expr param.
 *
 * @author edgar
 * @since 4.0.3
 */
public class VarParam implements Param {

  /** Value. */
  public final Variable fn;

  /**
   * Creates a new {@link VarParam}.
   *
   * @param value Value.
   */
  public VarParam(final Variable value) {
    this.fn = value;
  }

  @Override
  public Object apply(final Context context) throws IOException {
    return this.fn.value(context, new FastStringWriter());
  }

  @Override
  public String toString() {
    return fn.text();
  }
}
