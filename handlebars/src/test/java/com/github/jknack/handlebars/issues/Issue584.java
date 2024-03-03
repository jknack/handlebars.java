/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue584 extends v4Test {

  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.setPrettyPrint(true);
  }

  @Test
  public void shouldRemoveBlankAroundElse() throws IOException {
    shouldCompileTo(
        "A\n"
            + "{{#if someVariableWhichIsFalse}}\n"
            + "B\n"
            + "{{else}}\n"
            + "C\n"
            + "{{/if}}\n"
            + "D",
        $,
        "A\n" + "C\n" + "D");

    shouldCompileTo(
        "A\n"
            + "{{#if someVariableWhichIsFalse}}\n"
            + "B\n"
            + "{{^}}\n"
            + "C\n"
            + "{{/if}}\n"
            + "D",
        $,
        "A\n" + "C\n" + "D");
  }
}
