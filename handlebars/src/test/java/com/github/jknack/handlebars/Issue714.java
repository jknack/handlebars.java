/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.helper.StringHelpers;

public class Issue714 extends v4Test {

  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.registerHelpers(StringHelpers.class);
  }

  @Test
  public void shouldIgnoreEmptyString() throws IOException {
    shouldCompileTo("{{cut value \"-\"}}", $("hash", $("value", "")), "");

    shouldCompileTo("{{cut value \"-\"}}", $("hash", $("value", "2019-12-30")), "20191230");
  }
}
