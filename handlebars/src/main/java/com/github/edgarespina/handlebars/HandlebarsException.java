package com.github.edgarespina.handlebars;

/**
 * If something goes wrong this exception will happen.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class HandlebarsException extends RuntimeException {

  /**
   * The serial UUID.
   */
  private static final long serialVersionUID = -294368972176956335L;

  /**
   * Creates a new {@link HandlebarsException}.
   *
   * @param message The error's message.
   */
  public HandlebarsException(final String message) {
    super(message);
  }

  /**
   * Creates a new {@link HandlebarsException}.
   *
   * @param cause The error's cause.
   */
  public HandlebarsException(final Exception cause) {
    super(cause);
  }

  /**
   * Creates a new {@link HandlebarsException}.
   *
   * @param message The error's message.
   * @param cause The error's cause.
   */
  public HandlebarsException(final String message, final Exception cause) {
    super(message, cause);
  }
}
