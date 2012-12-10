/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;


public class EachKeyTest {

  public static class Blog {

    private String title;

    private String body;

    public Blog(final String title, final String body) {
      this.title = title;
      this.body = body;
    }

    public String getTitle() {
      return title;
    }

    public String getBody() {
      return body;
    }
  }
  @Test
  public void eachKeyWithString() throws IOException {
    Handlebars handlebars = new Handlebars();

    Template template = handlebars.compile("{{#each this}}{{@key}} {{/each}}");
    assertEquals("empty bytes ", template.apply("String"));
  }

  @Test
  public void eachKeyWithInt() throws IOException {
    Handlebars handlebars = new Handlebars();

    Template template = handlebars.compile("{{#each this}}{{@key}} {{/each}}");
    assertEquals("", template.apply(7));
  }

  @Test
  public void eachKeyWithJavaBean() throws IOException {
    Handlebars handlebars = new Handlebars();

    Template template = handlebars.compile("{{#each this}}{{@key}}: {{this}} {{/each}}");
    Blog blog = new Blog("Handlebars.java", "...");
    String expected1 = "body: ... title: Handlebars.java ";
    String expected2 = "title: Handlebars.java body: ... ";
    String result = template.apply(blog);
    assertTrue(result.equals(expected1) || result.equals(expected2));
  }

  @Test
  public void eachKeyWithHash() throws IOException {
    Handlebars handlebars = new Handlebars();

    Template template = handlebars.compile("{{#each this}}{{@key}}: {{this}} {{/each}}");
    Map<String, Object> hash = new LinkedHashMap<String, Object>();
    hash.put("body", "...");
    hash.put("title", "Handlebars.java");
    assertEquals("body: ... title: Handlebars.java ", template.apply(hash));
  }
}
