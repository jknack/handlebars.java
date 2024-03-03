/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.jknack.handlebars.Handlebars.Utils;

/**
 * Unit test for {@link Utils#isEmpty(Object)}
 *
 * @author edgar.espina
 */
public class NoFalsyValueTest {

  @ParameterizedTest
  @MethodSource("data")
  public void noFalsy(Object value) {
    assertEquals(false, Handlebars.Utils.isEmpty(value));
  }

  public static List<Object> data() {
    return Arrays.asList(
        new Object[] {new Object()},
        new Object[] {true},
        new Object[] {"Hi"},
        new Object[] {Boolean.TRUE},
        new Object[] {Arrays.asList(1)},
        new Object[] {new Object[] {1}},
        // Custom Iterable
        new Object[] {
          new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
              return Arrays.asList(1).iterator();
            }
          }
        });
  }
}
