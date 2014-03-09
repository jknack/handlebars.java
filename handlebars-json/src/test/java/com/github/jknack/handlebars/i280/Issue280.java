package com.github.jknack.handlebars.i280;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.JsonNodeValueResolver;

public class Issue280 extends AbstractTest {

  @Override
  protected Object configureContext(final Object context) {
    return Context.newBuilder(context).resolver(JsonNodeValueResolver.INSTANCE).build();
  }

  @Test
  public void errorWhileLookingParentContextUsingJsonNodeValueResolverFromArray() throws Exception {
    JsonNode node = new ObjectMapper().readTree("[{\"key\": \"value\"}]");
    shouldCompileTo("{{#each this}}{{undefinedKey}}{{/each}}", node, "");
  }

  @Test
  public void errorWhileLookingParentContextUsingJsonNodeValueResolverFromHash() throws Exception {
    JsonNode node = new ObjectMapper().readTree("{\"key\": \"value\"}");
    shouldCompileTo("{{#each this}}{{undefinedKey}}{{/each}}", node, "");
  }

}
