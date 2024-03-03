/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Hbs530 extends v4Test {

  @Test
  public void indexOutOfBoundsExceptioWhenUsingBlockParametersOnAnEmptyList() throws IOException {
    shouldCompileTo(
        "{{#each users as |user userId|}}\n"
            + "  Id: {{userId}} Name: {{user.name}} <BR>\n"
            + "{{/each}}",
        $("hash", $("users", Arrays.asList())),
        "");
  }
}
