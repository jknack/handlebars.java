/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue660 extends v4Test {

  @Test
  public void partialWithLineBreak() throws Exception {
    shouldCompileTo(
        "text text text\n"
            + "{{#>some-partial param1=\"val1\"\n"
            + "                 param2=\"val2\"}}\n"
            + "    default content\n"
            + "{{/some-partial}}",
        $,
        "text text text\n" + "\n" + "    default content\n");
  }
}
