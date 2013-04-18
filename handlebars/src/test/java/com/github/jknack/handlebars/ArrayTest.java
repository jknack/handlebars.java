/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    Hash hash = $("list", new String[]{"w", "o", "r", "l", "d" });
    shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
  }

  @Test
  public void objectArray() throws IOException {
    Hash hash = $("list", new Object[]{"w", "o", "r", "l", "d" });
    shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
  }

  @Test
  public void eachArray() throws IOException {
    Hash hash = $("list", new Object[]{"w", "o", "r", "l", "d" });
    shouldCompileTo("Hello {{#each list}}{{this}}{{/each}}!", hash, "Hello world!");
  }

  @Test
  public void letterArray() throws IOException {
    Hash hash = $("list", new Letter[]{new Letter('w'), new Letter('o'),
        new Letter('r'), new Letter('l'), new Letter('d') });
    shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
  }

  @Test
  public void arrayLength() throws IOException {
    Object[] array = {"1", 2, "3" };
    assertEquals(
        "3", compile("{{this.length}}").apply(
            Context.newBuilder(array).resolver(FieldValueResolver.INSTANCE).build()));
  }
}
