/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue500 extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers(
          "500.js",
          "Handlebars.registerHelper(\"chunk\",function(arr,size,options){\n"
              + "\n"
              + "\n"
              + "var newArr=['a', 'bc', 'd', size, arr.length];\n"
              + "\n"
              + "return newArr;\n"
              + "});");
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void shouldEachHelperIterateOverJSNativeArrays() throws IOException {
    shouldCompileTo(
        "{{#each (chunk array 2) }}{{this}}{{/each}}",
        $("hash", $("array", new String[] {"a", "b", "c", "d"})),
        "abcd24");

    shouldCompileTo(
        "{{#each (chunk array 2) }}{{this}}{{/each}}",
        $("hash", $("array", Arrays.asList("a", "b", "c", "d"))),
        "abcd24");
  }
}
