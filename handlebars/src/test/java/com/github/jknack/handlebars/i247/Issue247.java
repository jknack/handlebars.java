/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i247;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue247 extends AbstractTest {

  @Test
  public void dontResetPseudoVarsOnLoops() throws Exception {
    shouldCompileTo(
        "{{#each list1}}"
            + "before each: {{@index}} {{@first}} {{@last}}\n"
            + "{{#each list2}}"
            + "  {{@index}} {{@first}} {{@last}}\n"
            + "{{/each}}"
            + "after each: {{@index}} {{@first}} {{@last}}\n"
            + "{{/each}}",
        $("list1", new Object[] {"i1", "i2", "i3"}, "list2", new Object[] {"a", "b", "c"}),
        "before each: 0 first \n"
            + "  0 first \n"
            + "  1  \n"
            + "  2  last\n"
            + "after each: 0 first \n"
            + "before each: 1  \n"
            + "  0 first \n"
            + "  1  \n"
            + "  2  last\n"
            + "after each: 1  \n"
            + "before each: 2  last\n"
            + "  0 first \n"
            + "  1  \n"
            + "  2  last\n"
            + "after each: 2  last\n");
  }
}
