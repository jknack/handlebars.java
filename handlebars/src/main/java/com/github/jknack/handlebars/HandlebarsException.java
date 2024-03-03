/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

/**
 * If something goes wrong this exception will happen.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class HandlebarsException extends RuntimeException {

  /** The serial UUID. */
  private static final long serialVersionUID = -294368972176956335L;

  /** A handlebars error. Optional. */
  private HandlebarsError error;

  /**
   * Creates a new {@link HandlebarsException}.
   *
   * @param error The hbs error's. Required.
   */
  public HandlebarsException(final HandlebarsError error) {
    super(error.message);
    this.error = error;
  }

  /**
   * Creates a new {@link HandlebarsException}.
   *
   * @param cause The error's cause.
   */
  public HandlebarsException(final Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new {@link HandlebarsException}.
   *
   * @param error The error's message.
   * @param cause The error's cause.
   */
  public HandlebarsException(final HandlebarsError error, final Throwable cause) {
    super(error.message, cause);
    this.error = error;
  }

  /**
   * Creates a new {@link HandlebarsException}.
   *
   * @param message The error's message.
   * @param cause The error's cause.
   */
  public HandlebarsException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * A handlebars error.
   *
   * @return A handlebars error. It might be null.
   */
  public HandlebarsError getError() {
    return error;
  }
}
