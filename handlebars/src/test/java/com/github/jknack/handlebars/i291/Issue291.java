/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i291;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Issue291 extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    return super.newHandlebars()
        .registerHelperMissing(
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                return options.fn.position()[0] + ":" + options.fn.position()[1];
              }
            });
  }

  @Test
  public void sourceLocation() throws IOException {
    shouldCompileTo("hello {{world}}", $, "hello 1:8");
    shouldCompileTo("\nhello  {{world}}", $, "\nhello  2:9");
  }
}
