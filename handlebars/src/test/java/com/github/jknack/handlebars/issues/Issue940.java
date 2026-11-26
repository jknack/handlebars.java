/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.context.FieldValueResolver;

public class Issue940 {

  public static class Bean {
    private String secret = "hidden";
    public String open = "visible";
  }

  @Test
  public void shouldIgnoreNonPublicFieldsOfJdkClasses() throws IOException {
    Handlebars handlebars = new Handlebars();
    for (Object model : new Object[] {new HashMap<>(), new ArrayList<>(), "string"}) {
      Context context = Context.newBuilder(model).resolver(FieldValueResolver.INSTANCE).build();
      assertEquals(
          "[][][]", handlebars.compileInline("[{{size}}][{{table}}][{{modCount}}]").apply(context));
    }
  }

  @Test
  public void shouldKeepResolvingApplicationFields() throws IOException {
    Handlebars handlebars = new Handlebars();
    Context context = Context.newBuilder(new Bean()).resolver(FieldValueResolver.INSTANCE).build();
    assertEquals("hidden visible", handlebars.compileInline("{{secret}} {{open}}").apply(context));
  }

  @Test
  public void shouldFallThroughToNextResolver() throws IOException {
    Handlebars handlebars = new Handlebars();
    Map<String, Object> map = new HashMap<>();
    map.put("size", "from-map");
    Context context = Context.newBuilder(map).resolver(FieldValueResolver.INSTANCE).build();
    assertEquals("from-map", handlebars.compileInline("{{size}}").apply(context));
  }
}
