package com.github.edgarespina.handlerbars.internal;

/**
 * Callback interface for templates who need to know the start and end
 * delimiters in a specific time of execution.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
interface DelimAware {

  /**
   * Set the start and end delimiter.
   *
   * @param delimStart The start delimiter.
   * @param delimEnd The end delimiter.
   */
  void setDelimiters(String delimStart, String delimEnd);
}
