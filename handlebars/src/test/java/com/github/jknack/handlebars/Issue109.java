package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class Issue109 {

  @Test
  public void emptyStringMustacheBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("empty", "");
    assertEquals("", new Handlebars().compile("{{#empty}}truthy{{/empty}}").apply(context));
  }

  @Test
  public void emptyStringElseBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("empty", "");
    assertEquals("falsy", new Handlebars().compile("{{^empty}}falsy{{/empty}}").apply(context));
  }

  @Test
  public void emptyStringIfBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("empty", "");
    assertEquals("falsy", new Handlebars().compile("{{#if empty}}truthy{{else}}falsy{{/if}}").apply(context));
  }

  @Test
  public void noEmptyStringMustacheBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("nonempty", "xyz");
    assertEquals("truthy", new Handlebars().compile("{{#nonempty}}truthy{{/nonempty}}").apply(context));
  }

  @Test
  public void noEmptyStringElseBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("nonempty", "xyz");
    assertEquals("", new Handlebars().compile("{{^nonempty}}falsy{{/nonempty}}").apply(context));
  }

  @Test
  public void noEmptyStringIfBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    context.put("nonempty", "xyz");
    assertEquals("truthy", new Handlebars().compile("{{#if nonempty}}truthy{{/if}}").apply(context));
  }

  @Test
  public void nullMustacheBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    assertEquals("", new Handlebars().compile("{{#null}}truthy{{/null}}").apply(context));
  }

  @Test
  public void nullElseBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    assertEquals("falsy", new Handlebars().compile("{{^null}}falsy{{/null}}").apply(context));
  }

  @Test
  public void nullIfBlock() throws IOException {
    Map<String, Object> context = new HashMap<String, Object>();
    assertEquals("falsy", new Handlebars().compile("{{#if null}}truthy{{else}}falsy{{/if}}").apply(context));
  }
}
