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
package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ArrayAccessTest {

  @Test
  public void arrayAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{array.[0]}}");
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("array", new String[] {"s1", "s2" });
    assertEquals("s1", template.apply(context));
  }

  @Test
  public void listAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{list.[1]}}");
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("list", Arrays.asList("s1", "s2"));
    assertEquals("s2", template.apply(context));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void qualifiedListAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{list.[0].title}}");
    Map<String, Object> blog = new HashMap<String, Object>();
    blog.put("title", "First Post!");

    Map<String, Object> context = new HashMap<String, Object>();
    context.put("list", Arrays.asList(blog));
    assertEquals("First Post!", template.apply(context));
  }

  @Test
  public void ifArrayAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{#if array.[0]}}S1{{/if}}");
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("array", new String[] {"s1", "s2" });
    assertEquals("S1", template.apply(context));
  }

  @Test
  public void falsyArrayAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("{{#if array.[0]}}S1{{/if}}");
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("array", new String[] {"" });
    assertEquals("", template.apply(context));
  }
}
