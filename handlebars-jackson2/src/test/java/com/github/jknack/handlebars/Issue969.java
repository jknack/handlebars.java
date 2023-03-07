package com.github.jknack.handlebars;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;

import org.junit.Test;

public class Issue969 extends AbstractTest {

    @Override
    protected Object configureContext(final Object model) {
      return Context.newBuilder(model)
          .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE)
          .build();
    }

    @Override
    protected Handlebars newHandlebars() {
        return super.newHandlebars().with(EscapingStrategy.NOOP);
    }

    @Test
    public void shouldRecursivelyResolveEntries() throws IOException {
      Hash helpers = $("join", StringHelpers.join);
      JsonNode tree = new ObjectMapper().readTree("{\"pets\":[{\"type\":\"cat\",\"name\":\"alice\"},{\"type\":\"bird\",\"name\":\"bob\"}]}");
      shouldCompileTo("{{join this.pets \", \"}}", tree, helpers, "{type=cat, name=alice}, {type=bird, name=bob}");
    }

}
