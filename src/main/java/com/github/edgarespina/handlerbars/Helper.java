package com.github.edgarespina.handlerbars;

import java.io.IOException;

/**
 * Handlebars helpers can be accessed from any context in a template. You can
 * register a helper with the {@link Handlebars#registerHelper(String, Helper)}
 * method.
 *
 * @author edgar.espina
 * @param <T> The context object.
 * @since 0.1.0
 */
public interface Helper<T> {

  /**
   * Callback method for execute the given method.
   *
   * @param context The context object (param=0).
   * @param options The options object.
   * @return A string result.
   * @throws IOException If a template cannot be loaded.
   */
  CharSequence apply(T context, Options options) throws IOException;
}
