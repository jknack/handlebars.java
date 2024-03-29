/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i237;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue237 extends AbstractTest {

  public enum Status {
    NEW,
    DONE,
    CLOSED
  }

  @Test
  public void workForEnumMap() throws IOException {
    Map<Status, Integer> statuses = new EnumMap<>(Status.class);
    statuses.put(Status.NEW, 10);
    statuses.put(Status.DONE, 20);
    statuses.put(Status.CLOSED, 3);
    shouldCompileTo("{{statuses.NEW}}", $("statuses", statuses), "10");
  }

  @Test
  public void wontWorkForNormalMap() throws IOException {
    Map<Status, Integer> statuses = new HashMap<>();
    statuses.put(Status.NEW, 10);
    statuses.put(Status.DONE, 20);
    statuses.put(Status.CLOSED, 3);
    shouldCompileTo("{{statuses.NEW}}", $("statuses", statuses), "");
  }
}
