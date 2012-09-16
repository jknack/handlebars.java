/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.github.jknack.handlebars;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

/**
 * Useful information about a handlebar error.
 *
 * @author edgar.espina
 * @since 0.5.0
 */
public class HandlebarsError {

  /**
   * The error's line number.
   */
  public final int line;

  /**
   * The error's column number.
   */
  public final int column;

  /**
   * The error's problem.
   */
  public final String reason;

  /**
   * The error's evidence.
   */
  public final String evidence;

  /**
   * The file's name.
   */
  public final String filename;

  /**
   * The full error's message.
   */
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
  public HandlebarsError(final String filename, final int line,
      final int column, final String reason, final String evidence,
      final String message) {
    this.filename = notEmpty(filename, "The file's name is required");
    isTrue(line > 0, "The error's line number must be greather than zero");
    this.line = line;
    isTrue(column > 0, "The error's column number must be greather than zero");
    this.column = column;
    this.reason = notEmpty(filename, "The file's reason is required");
    this.evidence = notEmpty(filename, "The file's evidence is required");
    this.message = notEmpty(filename, "The file's message is required");
  }

  @Override
  public String toString() {
    return message;
  }
}
