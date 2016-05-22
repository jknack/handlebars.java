package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class MissingValueResolverTest {

  @Test
  public void useMissingValue() throws IOException {
    final Object hash = new Object();
    Handlebars handlebars = new Handlebars().registerHelperMissing(new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        assertEquals(hash, context);
        assertEquals("missingVar", options.helperName);
        return "(none)";
      }
    });

    assertEquals("(none)", handlebars.compileInline("{{missingVar}}").apply(hash));
  }

  @Test(expected = HandlebarsException.class)
  public void throwExceptionOnMissingValue() throws IOException {
    final Object hash = new Object();
    Handlebars handlebars = new Handlebars().registerHelperMissing(new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        throw new IllegalStateException("Missing variable: " + options.helperName);
      }
    });

    handlebars.compileInline("{{missingVar}}").apply(hash);
  }
}
