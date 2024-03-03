/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.context;

import org.junit.Test;

public class MethodValueResolverTest {

  public static class ExceptionalBean {
    public void getRTE() {
      throw new NullPointerException();
    }

    public void getCE() throws Exception {
      throw new Exception();
    }
  }

  @Test(expected = NullPointerException.class)
  public void mvrMustThrowRuntimeExceptions() {
    new MethodValueResolver().resolve(new ExceptionalBean(), "getRTE");
  }

  @Test(expected = IllegalStateException.class)
  public void mvrMustWrapCheckedExceptionAsRuntimeExceptions() {
    new MethodValueResolver().resolve(new ExceptionalBean(), "getCE");
  }
}
