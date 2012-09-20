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
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ArrayTest {

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
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("Hello {{#list}}{{this}}{{/list}}!");
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("list", new String[] {"w", "o", "r", "l", "d" });
    assertEquals("Hello world!", template.apply(context));
  }

  @Test
  public void objectArray() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("Hello {{#list}}{{this}}{{/list}}!");
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("list", new Object[] {"w", "o", "r", "l", "d" });
    assertEquals("Hello world!", template.apply(context));
  }

  @Test
  public void letterArray() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("Hello {{#list}}{{this}}{{/list}}!");
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("list", new Letter[] {new Letter('w'), new Letter('o'),
        new Letter('r'), new Letter('l'), new Letter('d') });
    assertEquals("Hello world!", template.apply(context));
  }
}
