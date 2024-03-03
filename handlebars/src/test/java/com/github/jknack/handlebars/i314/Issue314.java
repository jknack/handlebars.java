/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i314;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue314 extends AbstractTest {

  @Test
  public void withHelperSpec() throws IOException {
    String context = "{ obj: { context: { one: 1, two: 2 } } }";

    shouldCompileTo("{{#with obj}}{{context.one}}{{/with}}", context, "1");
    shouldCompileTo("{{#with obj}}{{obj.context.one}}{{/with}}", context, "");
  }
}
