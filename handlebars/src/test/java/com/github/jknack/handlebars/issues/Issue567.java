/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.util.Arrays;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue567 extends v4Test {

  @Test
  public void shouldKeepContextOnBlockParameter() throws Exception {
    shouldCompileTo(
        "{{#each foo as |v k|}}" + "  context is {{{.}}}" + "{{/each}}",
        $("hash", $("foo", Arrays.asList("a", "b", "c"))),
        "  context is {v=a, k=0}  context is {v=b, k=1}  context is {v=c, k=2}");
  }
}
