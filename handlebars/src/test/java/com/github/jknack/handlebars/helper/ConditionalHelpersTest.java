/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;

public class ConditionalHelpersTest {
  @Test
  public void compareLargeLongsWithoutRounding() throws IOException {
    Handlebars handlebars = new Handlebars().registerHelpers(ConditionalHelpers.class);
    assertEquals(
        "false/true",
        handlebars
            .compileInline("{{eq a b}}/{{neq a b}}")
            .apply(Map.of("a", 9007199254740992L, "b", 9007199254740993L)));
    assertFalse(ConditionalHelpers.eq.eq(Long.MAX_VALUE, Long.MAX_VALUE - 1));
    assertTrue(ConditionalHelpers.eq.eq(Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void compareBigNumbersWithoutRounding() {
    assertFalse(
        ConditionalHelpers.eq.eq(
            new BigInteger("9007199254740992"), new BigInteger("9007199254740993")));
    assertFalse(
        ConditionalHelpers.eq.eq(
            new BigDecimal("1.00000000000000001"), new BigDecimal("1.00000000000000002")));
    assertFalse(ConditionalHelpers.eq.eq(new BigDecimal("1e-400"), new BigDecimal("2e-400")));
    assertTrue(ConditionalHelpers.eq.eq(new BigDecimal("2.0"), new BigDecimal("2.00")));
  }

  @Test
  public void preserveOtherNumericComparisons() {
    assertTrue(ConditionalHelpers.eq.eq(2, 2.0));
    assertTrue(ConditionalHelpers.eq.eq(0.0, -0.0));
    assertTrue(ConditionalHelpers.eq.eq(0.0f, -0.0f));
    assertTrue(ConditionalHelpers.eq.eq(Double.NaN, Double.NaN));
    assertTrue(ConditionalHelpers.eq.eq(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    assertTrue(ConditionalHelpers.eq.eq(new AtomicLong(2), new AtomicLong(2)));
    assertFalse(ConditionalHelpers.eq.eq(null, 2));
    assertFalse(ConditionalHelpers.eq.eq("2", 2));
  }
}
