package com.github.jknack.handlebars.i338;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;

public class Issue338 extends AbstractTest {

  public static class HelperSource {

    public CharSequence each(final Object ctx, final Options options) {
      return "each";
    }

    public CharSequence unless(final Object ctx, final Options options) {
      return "unless";
    }

  }

  @Override
    protected void configure(final Handlebars handlebars) {
      handlebars.registerHelpers(new HelperSource());
    }

  @Test
  public void shouldNotFailOnOverride() throws IOException {
    shouldCompileTo("{{each}}", $, "each");
    shouldCompileTo("{{unless}}", $, "unless");
  }
}
