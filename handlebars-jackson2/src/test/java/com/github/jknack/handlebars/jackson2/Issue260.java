package com.github.jknack.handlebars.jackson2;

import java.io.IOException;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.context.MapValueResolver;

public class Issue260 extends AbstractTest {

  @Override
  protected Object configureContext(final Object model) {
    return Context.newBuilder(model)
        .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE).build();
  }

  @Test
  public void stringArray() throws IOException {
    JsonNode tree = new ObjectMapper().readTree("[\"a\", \"b\", \"c\"]");
    shouldCompileTo("{{#each this}}{{{.}}}{{/each}}", tree, "abc");
  }

  @Test
  public void hashStringArray() throws IOException {
    JsonNode tree = new ObjectMapper().readTree("{\"string\": [\"a\", \"b\", \"c\"]}");
    shouldCompileTo("{{#each string}}{{{.}}}{{/each}}", tree, "abc");
  }

  @Test
  public void hashIteratorStringArray() throws IOException {
    JsonNode tree = new ObjectMapper().readTree("{\"string\": [\"a\", \"b\", \"c\"]}");
    shouldCompileTo("{{#each this}}{{#each this}}{{{.}}}{{/each}}{{/each}}", tree, "abc");
  }
}
