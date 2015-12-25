package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.context.MapValueResolver;

public class Issue412 extends AbstractTest {

  @Override
  protected Object configureContext(final Object model) {
    return Context.newBuilder(model)
        .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE).build();
  }

  @Test
  public void keyShouldWork() throws IOException {
    JsonNode tree = new ObjectMapper().readTree("{\"firstName\":\"John\", \"lastName\":\"Smith\"}");
    shouldCompileTo("{{#each this}}{{@key}}: {{this}} {{/each}}", tree,
        "firstName: John lastName: Smith ");
  }

}
