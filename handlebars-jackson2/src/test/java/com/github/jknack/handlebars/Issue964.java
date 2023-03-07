package com.github.jknack.handlebars;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;

import org.junit.Test;

public class Issue964 extends AbstractTest {

    @Override
    protected Object configureContext(final Object model) {
      return Context.newBuilder(model)
          .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE)
          .build();
    }

    @Test
    public void shouldUnwrapJsonArraysAsIterables() throws IOException {
      Hash helpers = $("join", StringHelpers.join);
      JsonNode tree = new ObjectMapper().readTree("{\"pets\":[\"cat\",\"dog\",\"bird\"]}");
      shouldCompileTo("{{join this.pets \", \"}}", tree, helpers, "cat, dog, bird");
    }

    @Test
    public void shouldUnwrapJsonArraysByIndex() throws IOException {
      Hash helpers = $("join", StringHelpers.join);
      JsonNode tree = new ObjectMapper().readTree("{\"pets\":[\"cat\",\"dog\",\"bird\"]}");
      shouldCompileTo("{{join this.pets.[0] this.pets.[1] this.pets.[2] \", \"}}", tree, helpers, "cat, dog, bird");
    }

    @Test
    public void shouldUnwrapJsonArraysRecursively() throws IOException {
      Hash helpers = $("elementAt", new ElementAtHelper(), "capitalize", StringHelpers.capitalize);
      JsonNode tree = new ObjectMapper().readTree("{\"kidsPets\":[[\"cat\",\"dog\"],[\"bird\",\"mouse\"]]}");
      shouldCompileTo("{{capitalize (elementAt (elementAt this.kidsPets 0) 1)}}", tree, helpers, "Dog");
    }

    private static class ElementAtHelper implements Helper<Iterable<Object>> {

      @Override
      public Object apply(Iterable<Object> context, Options options) throws IOException {
        int targetIndex = options.param(0);
        int currentIndex = 0;

        Iterator<Object> loop = context.iterator();

        while (loop.hasNext()) {
          Object it = loop.next();
          if (currentIndex++ == targetIndex) {
            return it;
          }
        }

        throw new IOException(
          "Cannot get element at " + targetIndex + ". " +
          "Iterable only has " + currentIndex + " elements."
        );
      }

    }

}
