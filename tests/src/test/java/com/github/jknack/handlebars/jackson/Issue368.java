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

public class Issue368 extends AbstractTest {

  @Override
  protected Object configureContext(final Object context) {
    return Context.newBuilder(context)
        .resolver(JsonNodeValueResolver.INSTANCE, MapValueResolver.INSTANCE)
        .build();
  }

  @Test
  public void jsonItShouldHaveIndexVar() throws IOException {
    String value =
        "{ \"names\": [ { \"id\": \"a\" }, { \"id\": \"b\" }, { \"id\": \"c\" }, { \"id\": \"d\" }"
            + " ] }";

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(value);

    shouldCompileTo(
        "{{#each names}}index={{@index}}, id={{id}}, {{/each}}",
        jsonNode,
        "index=0, id=a, index=1, id=b, index=2, id=c, index=3, id=d, ");
  }
}
