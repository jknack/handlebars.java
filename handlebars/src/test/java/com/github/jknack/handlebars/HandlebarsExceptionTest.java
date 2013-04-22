package com.github.jknack.handlebars;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
    HandlebarsError error = createMock(HandlebarsError.class);
    HandlebarsException ex = new HandlebarsException(error, cause);
    assertEquals(cause, ex.getCause());
    assertEquals(error, ex.getError());
  }

  @Test
  public void withError() {
    HandlebarsError error = createMock(HandlebarsError.class);
    HandlebarsException ex = new HandlebarsException(error);
    assertEquals(error, ex.getError());
  }
}
