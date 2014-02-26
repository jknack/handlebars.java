package com.github.jknack.handlebars;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class Issue260 extends AbstractTest {

  @Override
  protected Object configureContext(final Object model) {
    return Context.newBuilder(model).resolver(JsonNodeValueResolver.INSTANCE).build();
  }

  @Test
  public void jsonStrings() throws IOException {
    JsonNode tree = new ObjectMapper().readTree("{\"string\": [\"a\", \"b\", \"c\"]}");
    shouldCompileTo("{{#each string}}{{{.}}}{{/each}}", tree, "abc");
  }
}
