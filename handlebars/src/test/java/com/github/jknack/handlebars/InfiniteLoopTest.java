/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.io.StringTemplateSource;

public class InfiniteLoopTest {

  @Test
  public void self() {
    assertThrows(HandlebarsException.class, () -> apply("r"));
  }

  @Test
  public void level1() {
    assertThrows(HandlebarsException.class, () -> apply("r1"));
  }

  @Test
  public void level2() {
    assertThrows(HandlebarsException.class, () -> apply("r2"));
  }

  @Test
  public void aliasedRecursionIsBounded() {
    Handlebars hb =
        new Handlebars(
            new com.github.jknack.handlebars.io.AbstractTemplateLoader() {
              @Override
              public com.github.jknack.handlebars.io.TemplateSource sourceAt(final String uri) {
                return new StringTemplateSource(
                    "/" + uri, "{{#with d}}{{> (lookup '.' 'p')}}{{/with}}");
              }
            });
    hb.setMaxPartialDepth(20);
    Map<String, Object> root = new LinkedHashMap<>();
    Map<String, Object> cur = root;
    for (int i = 0; i < 1000; i++) {
      Map<String, Object> n = new LinkedHashMap<>();
      cur.put("p", "x" + i + "/../a");
      cur.put("d", n);
      cur = n;
    }
    HandlebarsException ex =
        assertThrows(
            HandlebarsException.class,
            () -> hb.compileInline("{{> (lookup '.' 'p')}}").apply(root));
    assertTrue(ex.getMessage().contains("maximum partial depth"), ex.getMessage());
  }

  private void apply(final String path) throws IOException {
    try {
      new Handlebars().compile(path).apply(new Object());
    } catch (HandlebarsException ex) {
      Handlebars.log(ex.getMessage());
      throw ex;
    }
  }
}
