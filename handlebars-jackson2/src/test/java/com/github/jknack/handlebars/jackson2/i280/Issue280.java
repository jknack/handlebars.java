package com.github.jknack.handlebars.jackson2.i280;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.jackson2.JsonNodeValueResolver;

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
