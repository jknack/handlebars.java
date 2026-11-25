/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Options;

/** Argument validation for built-in block helpers. */
final class BuiltinArgs {

  private BuiltinArgs() {}

  /**
   * Fail if the block helper was called with more than one argument. No argument means the current
   * context. Skipped when the helper is invoked programmatically (argument count unknown).
   *
   * @param name Helper's name.
   * @param options The options.
   */
  static void requireAtMostOne(final String name, final Options options) {
    Integer size = options.data(Context.PARAM_SIZE);
    if (size != null && size > 1) {
      throw new IllegalArgumentException("#" + name + " accepts at most one argument");
    }
  }
}
