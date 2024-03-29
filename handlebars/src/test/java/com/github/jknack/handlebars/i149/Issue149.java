/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i149;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Issue149 extends AbstractTest {

  @Test
  public void negativeParam() throws IOException {
    shouldCompileTo(
        "{{neg foo -1}}",
        $("foo", "x"),
        $(
            "neg",
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                return "" + context + options.param(0);
              }
            }),
        "x-1");
  }

  @Test
  public void negativeHash() throws IOException {
    shouldCompileTo(
        "{{neg foo h=-1}}",
        $("foo", "x"),
        $(
            "neg",
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                return "" + context + options.hash("h");
              }
            }),
        "x-1");
  }
}
