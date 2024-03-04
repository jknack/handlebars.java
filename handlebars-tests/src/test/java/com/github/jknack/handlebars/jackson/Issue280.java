/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;

public class Issue280 extends AbstractTest {

  @Override
  protected Object configureContext(final Object context) {
    return Context.newBuilder(context).resolver(JsonNodeValueResolver.INSTANCE).build();
  }

  @Test
  public void errorWhileLookingParentContextUsingJsonNodeValueResolver() throws Exception {
    JsonNode node = new ObjectMapper().readTree("[{\"key\": \"value\"}]");
    shouldCompileTo("{{#each this}}{{undefinedKey}}{{/each}}", node, "");
  }
}
