/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class Issue1045 extends AbstractTest {

  @Test
  public void shouldRenderArrayWithAsBlockParam() throws IOException {
    shouldCompileTo(
        "{{#each (array 'a' 'b' 'c')}}" + "{{this}}\n" + "{{/each}}",
        $,
        $(
            "array",
            (Helper<Object>)
                (context, options) ->
                    Stream.concat(Stream.of(context), Stream.of(options.params)).toList()),
        "a\nb\nc\n");

    shouldCompileTo(
        "{{#with (array 'a' 'b' 'c') as |x|}}{{#each x}}" + "{{this}}\n" + "{{/each}}{{/with}}",
        $,
        $(
            "array",
            (Helper<Object>)
                (context, options) ->
                    Stream.concat(Stream.of(context), Stream.of(options.params)).toList()),
        "a\nb\nc\n");
  }
}
