/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i385;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue385 extends AbstractTest {

  @Test
  public void shouldHaveAccessToRoot() throws IOException {
    shouldCompileTo(
        "{{#each array}}{{@root.foo}}{{/each}}",
        $("foo", "bar", "array", new Object[] {"foo"}),
        "bar");
  }
}
