/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.util.Arrays;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue574 extends v4Test {

  @Test
  public void eachShouldExecuteElseBranchOnFalsyValue() throws Exception {
    shouldCompileTo(
        "{{#each list}}not empty{{else}}empty{{/each}}",
        $("hash", $("list", Arrays.asList())),
        "empty");

    shouldCompileTo(
        "{{#each list}}not empty{{else}}empty{{/each}}", $("hash", $("list", null)), "empty");
  }
}
