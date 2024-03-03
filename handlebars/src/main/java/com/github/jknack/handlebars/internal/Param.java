/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;

import com.github.jknack.handlebars.Context;

/**
 * Helper or hash param.
 *
 * @author edgar
 * @since 4.0.3
 */
public interface Param {

  /**
   * Apply the given param to context.
   *
   * @param context A context.
   * @return Param value.
   * @throws IOException If something goes wrong.
   */
  Object apply(Context context) throws IOException;
}
