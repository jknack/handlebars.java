/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i410;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Options;

public class Issue410 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelpers(this);
  }

  public CharSequence msg(final String msg, final Options options) {
    return msg + options.param(0) + options.param(1) + options.param(2);
  }

  public CharSequence msg1(final String msg, final Object p1, final Options options) {
    return msg + p1 + options.param(1) + options.param(2);
  }

  public CharSequence msgerr(final String msg, final Object p1, final Options options) {
    return msg + p1 + options.param(1) + options.param(2);
  }

  @Test
  public void shouldNotThrowArrayIndexOutOfBoundsException() throws IOException {
    shouldCompileTo("{{msg 'p' 1 2 3}}", $, "p123");

    shouldCompileTo("{{msg1 'p' 1 2 3}}", $, "p123");
  }

  @Test
  public void shouldNotThrowArrayIndexOutOfBoundsExceptionErr() throws IOException {
    assertThrows(HandlebarsException.class, () -> shouldCompileTo("{{msgerr 'p'}}", $, "p123"));
  }
}
