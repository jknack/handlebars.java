/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

public class HandlebarsExceptionTest {

  @Test
  public void withCause() {
    Exception cause = new NullPointerException();
    assertEquals(cause, new HandlebarsException(cause).getCause());
  }

  @Test
  public void withMessageCause() {
    Exception cause = new NullPointerException();
    String message = "message";
    HandlebarsException ex = new HandlebarsException(message, cause);
    assertEquals(cause, ex.getCause());
    assertEquals(message, ex.getMessage());
  }

  @Test
  public void withErrorCause() {
    Exception cause = new NullPointerException();
    HandlebarsError error = mock(HandlebarsError.class);
    HandlebarsException ex = new HandlebarsException(error, cause);
    assertEquals(cause, ex.getCause());
    assertEquals(error, ex.getError());
  }

  @Test
  public void withError() {
    HandlebarsError error = mock(HandlebarsError.class);
    HandlebarsException ex = new HandlebarsException(error);
    assertEquals(error, ex.getError());
  }
}
