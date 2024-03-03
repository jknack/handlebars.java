/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class MissingValueResolverTest {

  @Test
  public void useMissingValue() throws IOException {
    final Object hash = new Object();
    Handlebars handlebars =
        new Handlebars()
            .registerHelperMissing(
                new Helper<Object>() {
                  @Override
                  public Object apply(final Object context, final Options options)
                      throws IOException {
                    assertEquals(hash, context);
                    assertEquals("missingVar", options.helperName);
                    return "(none)";
                  }
                });

    assertEquals("(none)", handlebars.compileInline("{{missingVar}}").apply(hash));
  }

  @Test
  public void throwExceptionOnMissingValue() throws IOException {
    final Object hash = new Object();
    Handlebars handlebars =
        new Handlebars()
            .registerHelperMissing(
                new Helper<Object>() {
                  @Override
                  public Object apply(final Object context, final Options options)
                      throws IOException {
                    throw new IllegalStateException("Missing variable: " + options.helperName);
                  }
                });

    assertThrows(
        HandlebarsException.class, () -> handlebars.compileInline("{{missingVar}}").apply(hash));
  }
}
