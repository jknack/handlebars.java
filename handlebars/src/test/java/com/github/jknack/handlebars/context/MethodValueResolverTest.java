/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.context;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class MethodValueResolverTest {

  public static class ExceptionalBean {
    public void getRTE() {
      throw new NullPointerException();
    }

    public void getCE() throws Exception {
      throw new Exception();
    }
  }

  @Test
  public void mvrMustThrowRuntimeExceptions() {
    assertThrows(
        NullPointerException.class,
        () -> new MethodValueResolver().resolve(new ExceptionalBean(), "getRTE"));
  }

  @Test
  public void mvrMustWrapCheckedExceptionAsRuntimeExceptions() {
    assertThrows(
        IllegalStateException.class,
        () -> new MethodValueResolver().resolve(new ExceptionalBean(), "getCE"));
  }
}
