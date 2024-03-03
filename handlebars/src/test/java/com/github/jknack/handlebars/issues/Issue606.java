/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue606 extends v4Test {

  public static class Helpers {

    public int subtract(int a, int b) {
      return a - b;
    }
  }

  @Override
  protected void configure(Handlebars handlebars) {
    super.configure(handlebars);
    handlebars.registerHelpers(new Helpers());
  }

  @Test
  public void shouldSupportNoneCharSequenceReturnsTypeFromHelperClass() throws Exception {
    shouldCompileTo("{{#if (subtract value 1)}}OK{{/if}}", $("hash", $("value", 2)), "OK");
    shouldCompileTo("{{#if (subtract value 1)}}OK{{/if}}", $("hash", $("value", 1)), "");
  }
}
