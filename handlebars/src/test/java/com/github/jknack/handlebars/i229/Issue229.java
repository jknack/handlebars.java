package com.github.jknack.handlebars.i229;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;

public class Issue229 extends AbstractTest {

  @Test
  public void args() throws IOException {
    Context context = Context.newContext(null);
    context.data("data", new Object() {
      @SuppressWarnings("unused")
      public String getContext() {
        return "Ok!";
      }
    });
    shouldCompileTo("{{@data.context}}", context, "Ok!");
  }
}
