/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class EachObjectWithLast extends AbstractTest {

  @Test
  public void eachObjectWithLast() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}{{#if @last}}{{text}}! {{/if}}{{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            $("foo", $("text", "goodbye"), "bar", $("text", "Goodbye")),
            "world",
            "world"),
        "Goodbye! cruel world!");
  }
}
