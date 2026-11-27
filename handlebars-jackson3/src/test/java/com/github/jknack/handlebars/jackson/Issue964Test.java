/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class Issue964Test {

  private String render(final String template, final String json) throws IOException {
    Handlebars handlebars = new Handlebars().with(EscapingStrategy.NOOP);
    handlebars.registerHelper("join", StringHelpers.join);
    JsonNode tree = JsonMapper.builder().build().readTree(json);
    Context ctx =
        Context.newBuilder(tree)
            .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE)
            .build();
    return handlebars.compileInline(template).apply(ctx);
  }

  @Test
  void arraysAreIterables() throws IOException {
    assertEquals(
        "cat, dog, bird",
        render("{{join this.pets \", \"}}", "{\"pets\":[\"cat\",\"dog\",\"bird\"]}"));
  }

  @Test
  void arraysByIndex() throws IOException {
    assertEquals("dog", render("{{this.pets.[1]}}", "{\"pets\":[\"cat\",\"dog\",\"bird\"]}"));
  }

  @Test
  void entriesAreResolvedRecursively() throws IOException {
    assertEquals(
        "{type=cat, name=alice}, {type=bird, name=bob}",
        render(
            "{{join this.pets \", \"}}",
            "{\"pets\":[{\"type\":\"cat\",\"name\":\"alice\"},{\"type\":\"bird\",\"name\":\"bob\"}]}"));
  }

  @Test
  void eachOverObject() throws IOException {
    assertEquals(
        "a=1;b=x;", render("{{#each this}}{{@key}}={{this}};{{/each}}", "{\"a\":1,\"b\":\"x\"}"));
  }
}
