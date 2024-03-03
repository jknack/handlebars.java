/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue200 extends AbstractTest {

  @Test
  public void actualBug() throws IOException {
    Handlebars h = newHandlebars();
    h.registerHelper(
        "replaceHelperTest",
        new Helper<String>() {
          @Override
          public Object apply(final String text, final Options options) {
            return "foo";
          }
        });

    h.registerHelpers(new DynamicHelperExample());
    Template t = h.compileInline("hello world: {{replaceHelperTest \"foobar\"}}");

    assertEquals("hello world: bar", t.apply(null));
  }
}
