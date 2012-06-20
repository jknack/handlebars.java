package com.github.edgarespina.handlerbars.internal;

/**
 * Mustache/Handlabars are contextual template engines. This class represent the
 * 'context stack' of a template.
 * <ul>
 * <li>Objects and hashes should be pushed onto the context stack.
 * <li>All elements on the context stack should be accessible.
 * <li>Multiple sections per template should be permitted.
 * <li>Failed context lookups should be considered falsey.
 * <li>Dotted names should be valid for Section tags.
 * <li>Dotted names that cannot be resolved should be considered falsey.
 * <li>Dotted Names - Context Precedence: Dotted names should be resolved
 * against former resolutions.
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public interface Context {
  /**
   * Resolved as '.' or 'this' inside templates. Required.
   *
   * @return The target value
   */
  Object target();

  /**
   * Lookup the given key inside the context stack.
   * <ul>
   * <li>Objects and hashes should be pushed onto the context stack.
   * <li>All elements on the context stack should be accessible.
   * <li>Multiple sections per template should be permitted.
   * <li>Failed context lookups should be considered falsey.
   * <li>Dotted names should be valid for Section tags.
   * <li>Dotted names that cannot be resolved should be considered falsey.
   * <li>Dotted Names - Context Precedence: Dotted names should be resolved
   * against former resolutions.
   * </ul>
   *
   * @param key The object' key.
   * @return The value associated to the given key or <code>null</code> if no
   *         value is found.
   */
  Object get(final Object key);
}
