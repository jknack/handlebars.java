/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.context.MapValueResolver;

public class Issue996 extends AbstractTest {

  @Override
  protected Object configureContext(final Object model) {
    return Context.newBuilder(model)
        .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE)
        .build();
  }

  @Test
  public void shouldSupportIncludeZeroOptionIfHelper() throws IOException {
    JsonNode tree = new ObjectMapper().readTree("[1, 2, 3]");
    shouldCompileTo(
        "{{#if 0}}true condition{{else}}false condition{{/if}}", tree, "false condition");
    shouldCompileTo(
        "{{#if 0 includeZero=true}}true condition{{else}}false condition{{/if}}",
        tree,
        "true condition");
    shouldCompileTo(
        "{{#if 0 includeZero=false}}true condition{{else}}false condition{{/if}}",
        tree,
        "false condition");
  }
}
