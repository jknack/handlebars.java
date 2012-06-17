package com.github.edgarespina.handlerbars;

public class HandlebarsException extends RuntimeException {

  /**
   * The serial UUID.
   */
  private static final long serialVersionUID = -294368972176956335L;

  public HandlebarsException(final String message) {
    super(message);
  }

  public HandlebarsException(final Exception ex) {
    super(ex);
  }

  public HandlebarsException(final String message, final Exception ex) {
    super(message, ex);
  }
}
