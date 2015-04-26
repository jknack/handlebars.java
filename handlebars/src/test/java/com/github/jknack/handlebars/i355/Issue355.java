package com.github.jknack.handlebars.i355;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.StringHelpers;

public class Issue355 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelpers(StringHelpers.class);
  }

  @Test
  public void shouldFormatZero() throws IOException {
    shouldCompileTo("{{numberFormat 0}}", $, "0");

    shouldCompileTo("{{numberFormat 0 'currency'}}", $, "$0.00");

    shouldCompileTo("{{numberFormat price 'currency'}}", $("price", 0.0), "$0.00");
  }
}
