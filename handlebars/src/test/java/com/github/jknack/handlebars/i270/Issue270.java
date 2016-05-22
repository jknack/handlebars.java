package com.github.jknack.handlebars.i270;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Issue270 extends AbstractTest {

  @Test
  public void charLiteral() throws IOException {
    shouldCompileTo("{{modifiers this 'clock'}}", $, $("modifiers", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return options.params[0].toString();
      }
    }), "clock");
  }
}
