/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.EnumMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.context.MapValueResolver;

public class Issue1083 extends AbstractTest {

  public enum Status {
    NEW {
      @Override
      public boolean isNew() {
        return true;
      }
    },
    DONE;

    public boolean isNew() {
      return false;
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{{statuses.NEW}} {{statuses.DONE}}",
        "{{statuses.[NEW]}} {{statuses.[DONE]}}",
        "{{lookup statuses 'NEW'}} {{lookup statuses 'DONE'}}",
        "{{lookup statuses first}} {{lookup statuses second}}"
      })
  public void lookupEnumConstantsWithClassBodies(final String template) throws IOException {
    EnumMap<Status, Integer> statuses = new EnumMap<>(Status.class);
    statuses.put(Status.NEW, 10);
    statuses.put(Status.DONE, 20);
    shouldCompileTo(
        template, $("statuses", statuses, "first", Status.NEW, "second", Status.DONE), "10 20");
  }

  @Test
  public void missingEnumEntryIsUnresolved() throws IOException {
    EnumMap<Status, Integer> statuses = new EnumMap<>(Status.class);
    statuses.put(Status.NEW, 10);
    shouldCompileTo("{{statuses.[DONE]}}", $("statuses", statuses), "");
  }

  @Test
  public void nullEnumEntryIsUnresolved() throws IOException {
    EnumMap<Status, Integer> statuses = new EnumMap<>(Status.class);
    statuses.put(Status.NEW, null);
    shouldCompileTo("{{statuses.[NEW]}}", $("statuses", statuses), "");
  }

  @Test
  public void emptyEnumMapIsUnresolved() throws IOException {
    shouldCompileTo("{{statuses.[NEW]}}", $("statuses", new EnumMap<>(Status.class)), "");
  }

  @Test
  public void unknownEnumConstantStillThrows() {
    EnumMap<Status, Integer> statuses = new EnumMap<>(Status.class);
    statuses.put(Status.NEW, 10);
    assertThrows(
        IllegalArgumentException.class,
        () -> MapValueResolver.INSTANCE.resolve(statuses, "UNKNOWN"));
  }
}
