/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue458 extends v4Test {

  static class Foo {
    private String baa;

    public Foo(final String baa) {
      this.baa = baa;
    }

    public String getBaa() {
      return baa;
    }
  }

  @Test
  public void shouldNotRenderNullValues() throws IOException {
    shouldCompileTo("{{baa}}", $("hash", new Foo(null)), "");

    shouldCompileTo("{{this.baa}}", $("hash", new Foo(null)), "");

    shouldCompileTo("{{#with this}}{{baa}}{{/with}}", $("hash", new Foo(null)), "");
  }
}
