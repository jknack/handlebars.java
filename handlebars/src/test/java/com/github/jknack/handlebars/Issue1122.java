/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue1122 extends AbstractTest {

  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.registerHelperMissing(
        new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) {
            return options.fn.text();
          }
        });
  }

  @Test
  public void preserveWhitSpaceOnMissingHelper() throws IOException {
    shouldCompileTo("{{myvar}}{{ myvar2 }}", $, "{{myvar}}{{ myvar2 }}");
  }
}
