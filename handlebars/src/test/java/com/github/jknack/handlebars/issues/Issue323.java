/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Issue323 extends AbstractTest {

  @Test
  public void subExpressionInHashArg() throws IOException {
    shouldCompileTo(
        "{{someTemplate param1 param2 hashArg=(myHelper param3)}}",
        $("param1", "a", "param2", "b", "param3", "c"),
        $(
            "someTemplate",
            new Helper<String>() {
              @Override
              public Object apply(final String context, final Options options) throws IOException {
                return context + options.param(0) + options.hash("hashArg");
              }
            },
            "myHelper",
            new Helper<String>() {
              @Override
              public Object apply(final String context, final Options options) throws IOException {
                return context;
              }
            }),
        "abc");
  }
}
