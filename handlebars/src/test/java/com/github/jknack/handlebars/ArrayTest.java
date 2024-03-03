/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.context.FieldValueResolver;

public class ArrayTest extends AbstractTest {

  private static class Letter {
    private char letter;

    public Letter(final char letter) {
      this.letter = letter;
    }

    @Override
    public String toString() {
      return letter + "";
    }
  }

  @Test
  public void stringArray() throws IOException {
    Hash hash = $("list", new String[] {"w", "o", "r", "l", "d"});
    shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
  }

  @Test
  public void objectArray() throws IOException {
    Hash hash = $("list", new Object[] {"w", "o", "r", "l", "d"});
    shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
  }

  @Test
  public void eachArray() throws IOException {
    Hash hash = $("list", new Object[] {"w", "o", "r", "l", "d"});
    shouldCompileTo("Hello {{#each list}}{{this}}{{/each}}!", hash, "Hello world!");
  }

  @Test
  public void letterArray() throws IOException {
    Hash hash =
        $(
            "list",
            new Letter[] {
              new Letter('w'), new Letter('o'), new Letter('r'), new Letter('l'), new Letter('d')
            });
    shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
  }

  @Test
  public void arrayLength() throws IOException {
    Object[] array = {"1", 2, "3"};
    assertEquals(
        "3",
        compile("{{this.length}}")
            .apply(Context.newBuilder(array).resolver(FieldValueResolver.INSTANCE).build()));
  }
}
