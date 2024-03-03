/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.jknack.handlebars.Handlebars.Utils;

/**
 * Unit test for {@link Utils}
 *
 * @author edgar.espina
 */
public class FalsyValueTest {

  @ParameterizedTest
  @MethodSource("data")
  public void falsy(Object value) {
    assertEquals(true, Handlebars.Utils.isEmpty(value));
  }

  @Test
  public void emptyArray() {
    assertEquals(true, Handlebars.Utils.isEmpty(new Object[0]));
  }

  public static Stream<Object> data() {
    return Stream.of(
        null,
        false,
        "",
        Boolean.FALSE,
        0,
        (short) 0,
        0L,
        0F,
        0D,
        BigInteger.ZERO,
        BigDecimal.ZERO,
        Collections.emptyList(),
        new Iterable<Object>() {
          @Override
          public Iterator<Object> iterator() {
            return Collections.emptyList().iterator();
          }
        });
  }
}
