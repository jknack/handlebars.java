/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

public class Issue332 extends AbstractTest {

  static final long now = System.currentTimeMillis();

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.with(
        new Formatter() {

          @Override
          public Object format(final Object value, final Chain chain) {
            if (value instanceof Date) {
              return ((Date) value).getTime();
            }
            return chain.format(value);
          }
        });
  }

  @Test
  public void resolveThis() throws IOException {

    shouldCompileTo("time is {{this}}", new Date(now), "time is " + now);
  }

  @Test
  public void resolveNamed() throws IOException {

    shouldCompileTo("time is {{now}}", $("now", new Date(now)), "time is " + now);
  }

  @Test
  public void resolvePath() throws IOException {

    shouldCompileTo("time is {{this.now}}", $("now", new Date(now)), "time is " + now);
  }
}
