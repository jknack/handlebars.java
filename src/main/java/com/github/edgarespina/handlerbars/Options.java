package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.util.Map.Entry;

/**
 * Options available for {@link Helper#apply(Object, Options)}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public interface Options {

  /**
   * Apply the template function using the default context.
   *
   * @return A string output.
   * @throws IOException If a resource cannot be loaded.
   */
  String fn() throws IOException;

  /**
   * Apply the template function using the provided context.
   *
   * @param context The context to use.
   * @return A string output.
   * @throws IOException If a resource cannot be loaded.
   */
  String fn(Object context) throws IOException;

  /**
   * Apply the template function using the default context.
   *
   * @return A string output.
   * @throws IOException If a resource cannot be loaded.
   */
  String inverse() throws IOException;

  /**
   * Apply the template function using the provided context.
   *
   * @param context The context to use.
   * @return A string output.
   * @throws IOException If a resource cannot be loaded.
   */
  String inverse(Object context) throws IOException;

  /**
   * Find the parameter at the given position.
   *
   * @param index The parameter position.
   * @return The paramater's value.
   * @throws ArrayIndexOutOfBoundsException If the parameter index is invalid.
   */
  <T> T param(int index);

  /**
   * The number of parameter available.
   *
   * @return The number of parameter available.
   */
  int paramSize();

  /**
   * Find a value inside the hash attributes.
   *
   * @param name The hash's name.
   * @return The hash value or null.
   */
  <T> T hash(String name);

  /**
   * List all the hash.
   *
   * @return All the hash.
   */
  Iterable<Entry<String, Object>> hash();

  /**
   * Returns false if its argument is false, null or empty list/array (a "falsy"
   * value).
   *
   * @param value A value.
   * @return False if its argument is false, null or empty list/array (a "falsy"
   *         value).
   */
  boolean isEmpty(Object value);
}
