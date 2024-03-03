/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PropertyAccessTest {

  @Test
  public void arrayAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{array.[0]}}");
    Map<String, Object> context = new HashMap<>();
    context.put("array", new String[] {"s1", "s2"});
    assertEquals("s1", template.apply(context));
  }

  @Test
  public void listAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{list.[1]}}");
    Map<String, Object> context = new HashMap<>();
    context.put("list", Arrays.asList("s1", "s2"));
    assertEquals("s2", template.apply(context));
  }

  @Test
  public void listArrayIndexOutOfBoundsShouldResolveAsEmpty() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{list.[10]}}");
    Map<String, Object> context = new HashMap<>();
    context.put("list", Arrays.asList("s1", "s2"));
    assertEquals("", template.apply(context));
  }

  @Test
  public void listIndexOutOfBoundsShouldResolveAsEmpty() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{list.[10]}}");
    Map<String, Object> context = new HashMap<>();
    context.put("list", new ArrayList<>(Arrays.asList("s1", "s2")));
    assertEquals("", template.apply(context));
  }

  @Test
  public void arrayIndexOutOfBoundsShouldResolveAsEmpty() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{list.[10]}}");
    Map<String, Object> context = new HashMap<>();
    context.put("list", new String[] {"s1", "s2"});
    assertEquals("", template.apply(context));
  }

  @Test
  public void qualifiedListAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{list.[0].title}}");
    Map<String, Object> blog = new HashMap<>();
    blog.put("title", "First Post!");

    Map<String, Object> context = new HashMap<>();
    context.put("list", Arrays.asList(blog));
    assertEquals("First Post!", template.apply(context));
  }

  @Test
  public void ifArrayAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{#if array.[0]}}S1{{/if}}");
    Map<String, Object> context = new HashMap<>();
    context.put("array", new String[] {"s1", "s2"});
    assertEquals("S1", template.apply(context));
  }

  @Test
  public void falsyArrayAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("{{#if array.[0]}}S1{{/if}}");
    Map<String, Object> context = new HashMap<>();
    context.put("array", new String[] {""});
    assertEquals("", template.apply(context));
  }

  @Test
  public void notJavaNameAccess() throws IOException {
    Handlebars handlebars = new Handlebars();
    Map<String, Object> context = new HashMap<>();
    context.put("1foo", "foo");
    context.put("'foo'", "foo");
    context.put("foo or bar", "foo");
    context.put("foo.bar", "foo");
    context.put("134", "134");

    // assertEquals("foo",
    // handlebars.compileInline("{{this.[1foo]}}").apply(context));
    // assertEquals("foo",
    // handlebars.compileInline("{{this.['foo']}}").apply(context));
    // assertEquals("foo", handlebars.compileInline("{{this.[foo or bar]}}")
    // .apply(context));
    // assertEquals("foo",
    // handlebars.compileInline("{{this.[foo.bar]}}").apply(context));
    assertEquals("134", handlebars.compileInline("{{this.[134]}}").apply(context));
  }
}
