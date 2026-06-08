/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i820;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue820 extends v4Test {

  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.registerHelper("greeting", (context, options) -> "Hello");
  }

  /** A helper that takes no arguments should still work within a block. */
  @Test
  public void helperWithoutArgumentsUsedInsideEachBlock() throws Exception {
    shouldCompileTo(
        "{{greeting}}\n{{#each users as |user|}}{{greeting}} {{user}}\n{{/each}}",
        $("hash", $("users", new Object[] {"Jack", "John"})),
        "Hello\nHello Jack\nHello John\n");
  }
}
