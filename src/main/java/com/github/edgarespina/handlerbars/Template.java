package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.io.Writer;

/**
 * A compiled template created by {@link Handlebars#compile(String)}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public interface Template {

  /**
   * Merge the template tree using the given context.
   *
   * @param context The context object. May be null.
   * @param writer The writer object. Required.
   * @throws IOException If a resource cannot be loaded.
   */
  void apply(Object context, Writer writer) throws IOException;

  /**
   * Merge the template tree using the given context.
   *
   * @param context The context object. May be null.
   * @return The resulting template.
   * @throws IOException If a resource cannot be loaded.
   */
  String apply(Object context) throws IOException;

  /**
   * Provide the raw text.
   *
   * @return The raw text.
   */
  String rawText();
}
