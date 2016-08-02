package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.v4Test;

public class Hbs525 extends v4Test {

  @Test
  public void helperNameSpace() throws IOException {
    shouldCompileTo("{{nav.render 'main'}}", $("helpers", $("nav.render", new Helper<String>() {
      @Override
      public Object apply(final String context, final Options options) throws IOException {
        return context;
      }
    })), "main");
  }

}
