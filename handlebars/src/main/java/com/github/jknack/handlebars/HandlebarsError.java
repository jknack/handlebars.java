/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.Serializable;

/**
 * Useful information about a handlebar error.
 *
 * @author edgar.espina
 * @since 0.5.0
 */
public class HandlebarsError implements Serializable {

  /** The error's line number. */
  public final int line;

  /** The error's column number. */
  public final int column;

  /** The error's problem. */
  public final String reason;

  /** The error's evidence. */
  public final String evidence;

  /** The file's name. */
  public final String filename;

  /** The full error's message. */
  public final String message;

  /**
   * Creates a new {@link HandlebarsError}.
   *
   * @param filename The file's name. Required.
   * @param line The error's line number.
   * @param column The error's column number.
   * @param reason The error's reason. Required.
   * @param evidence The error's evidence. Required.
   * @param message The error's message. Required.
   */
  public HandlebarsError(
      final String filename,
      final int line,
      final int column,
      final String reason,
      final String evidence,
      final String message) {
    this.filename = notEmpty(filename, "The file's name is required");
    isTrue(line > 0, "The error's line number must be greater than zero");
    this.line = line;
    isTrue(column > 0, "The error's column number must be greater than zero");
    this.column = column;
    this.reason = notEmpty(reason, "The file's reason is required");
    this.evidence = notEmpty(evidence, "The file's evidence is required");
    this.message = notEmpty(message, "The file's message is required");
  }

  @Override
  public String toString() {
    return message;
  }
}
