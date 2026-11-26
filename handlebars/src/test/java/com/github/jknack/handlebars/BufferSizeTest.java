/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class BufferSizeTest {

  @Test
  public void defaults() {
    Handlebars hbs = new Handlebars();
    assertEquals(1600, hbs.bufferSize());
    assertEquals(128, hbs.blockBufferSize());
    assertEquals(32, hbs.subExpressionBufferSize());
  }

  @Test
  public void customSizes() throws IOException {
    Handlebars hbs = new Handlebars().bufferSize(1).blockBufferSize(1).subExpressionBufferSize(1);
    assertEquals(1, hbs.bufferSize());
    assertEquals(1, hbs.blockBufferSize());
    assertEquals(1, hbs.subExpressionBufferSize());

    Template template =
        hbs.compileInline(
            "{{#each items}}[{{#with this}}{{lookup ../this (lookup this"
                + " 'k')}}{{/with}}]{{/each}}");
    assertEquals(
        "[a][b]",
        template.apply(
            Map.of(
                "items", Arrays.asList(Map.of("k", "v", "v", "a"), Map.of("k", "v", "v", "b")))));
  }

  @Test
  public void mustBePositive() {
    Handlebars hbs = new Handlebars();
    assertThrows(IllegalArgumentException.class, () -> hbs.setBufferSize(0));
    assertThrows(IllegalArgumentException.class, () -> hbs.setBlockBufferSize(-1));
    assertThrows(IllegalArgumentException.class, () -> hbs.setSubExpressionBufferSize(0));
    assertThrows(IllegalArgumentException.class, () -> new FastStringWriter(0));
  }

  @Test
  public void writers() throws IOException {
    Writer writer = new FastStringWriter(1);
    writer.write("abc");
    assertEquals("abc", writer.toString());

    writer = new FastStringWriter();
    writer.append('x');
    assertEquals("x", writer.toString());
  }
}
