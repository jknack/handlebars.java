/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue1189 extends AbstractTest {

  /**
   * A standalone inverted section ({@code {{^helper args}}...{{/helper}}}) invoking a custom block
   * helper must pass through the helper's context (first param) and hash arguments the same way the
   * matching positive section ({@code {{#helper args}}...{{/helper}}}) does.
   */
  @Test
  public void unlessPassesContextAndHashArgumentsToHelper() throws IOException {
    Helper<Object> helper =
        (context, options) -> "context=" + context + ",scope=" + options.hash("scope");

    shouldCompileTo(
        "{{^myHelper foo scope=\"deps\"}}{{/myHelper}}",
        $("foo", "bar"),
        $("myHelper", helper),
        "context=bar,scope=deps");
  }

  /**
   * A second positional param (beyond the first, which becomes the context) must also flow through.
   */
  @Test
  public void unlessPassesExtraParamsToHelper() throws IOException {
    Helper<Object> helper =
        (context, options) -> "context=" + context + ",param0=" + options.param(0);

    shouldCompileTo(
        "{{^myHelper foo bar}}{{/myHelper}}",
        $("foo", "a", "bar", "b"),
        $("myHelper", helper),
        "context=a,param0=b");
  }
}
