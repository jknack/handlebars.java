package com.github.jknack.handlebars.i314;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue314 extends AbstractTest {

  @Test
  public void withHelperSpec() throws IOException {
    String context = "{ obj: { context: { one: 1, two: 2 } } }";

    shouldCompileTo("{{#with obj}}{{context.one}}{{/with}}", context, "1");
    shouldCompileTo("{{#with obj}}{{obj.context.one}}{{/with}}", context, "");
  }
}
