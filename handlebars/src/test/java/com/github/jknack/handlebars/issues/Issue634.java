/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.v4Test;

public class Issue634 extends v4Test {

  @Test
  public void shouldThrowHandlebarsExceptionWhenPartialBlockIsMissing() throws Exception {
    try {
      shouldCompileTo(
          "{{> my-partial}}", $("partials", $("my-partial", "Hello {{> @partial-block}}")), null);
      fail("Must throw HandlebarsException");
    } catch (HandlebarsException x) {
      assertTrue(x.getMessage().contains("does not provide a @partial-block"));
    }
  }

  @Test
  public void shouldNotThrowHandlebarsException() throws Exception {
    shouldCompileTo(
        "{{#> my-partial}}634{{/my-partial}}",
        $("partials", $("my-partial", "Hello {{> @partial-block}}")),
        "Hello 634");
  }
}
