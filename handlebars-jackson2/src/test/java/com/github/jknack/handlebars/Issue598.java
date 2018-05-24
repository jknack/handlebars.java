package com.github.jknack.handlebars;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.context.MapValueResolver;
import org.junit.Test;

import java.io.IOException;

public class Issue598 extends AbstractTest {

  @Override
  protected Object configureContext(final Object model) {
    return Context.newBuilder(model)
        .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE).build();
  }

  @Test
  public void shouldExtendJsonArrayWithLenghtProperty() throws IOException {
    JsonNode tree = new ObjectMapper().readTree("[1, 2, 3]");
    shouldCompileTo("{{this.length}}", tree, "3");
  }

}
