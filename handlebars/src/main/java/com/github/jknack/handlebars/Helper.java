/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

/**
 * Handlebars helpers can be accessed from any context in a template. You can register a helper with
 * the {@link Handlebars#registerHelper(String, Helper)} method.
 *
 * @author edgar.espina
 * @param <T> The context object.
 * @since 0.1.0
 */
public interface Helper<T> {

  /**
   * Apply the helper to the context.
   *
   * @param context The context object.
   * @param options The options object.
   * @return A string result.
   * @throws IOException If a template cannot be loaded.
   */
  Object apply(T context, Options options) throws IOException;
}
