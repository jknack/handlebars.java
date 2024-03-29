/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Unit test for {@link Locales}. */
public class LocalesTest {

  private static List<Locale> COMMON_LOCALES =
      Arrays.asList(
          Locale.CANADA,
          Locale.CANADA_FRENCH,
          Locale.CHINA,
          Locale.ENGLISH,
          Locale.GERMANY,
          Locale.FRANCE);

  @Test
  public void testUnderscore() {
    for (Locale l : COMMON_LOCALES) {
      // l.toString() returns format de_DE
      assertEquals(l, Locales.fromString(l.toString()));
    }
  }

  @Test
  public void testHyphen() {
    for (Locale l : COMMON_LOCALES) {
      // l.toLanguageTag() returns format de-DE
      assertEquals(l, Locales.fromString(l.toLanguageTag()));
    }
  }

  @Test
  public void testNull() {
    assertNull(Locales.fromString(null));
  }
}
