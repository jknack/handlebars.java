/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.util.IllformedLocaleException;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

/** Utility methods to resolve {@link Locale}s from Strings. */
public final class Locales {

  /** Not allowed. */
  private Locales() {}

  /**
   * Converts {@link String}s to {@link Locale}s. Supports both <a
   * href="https://en.wikipedia.org/wiki/IETF_language_tag">IETF language tags</a> using hyphens
   * (e.g. &quot;de-AT&quot;) and the Java format using underscores (e.g. &quot;de_AT&quot;).
   *
   * @param string the local string either in IETF or Java format
   * @return The matching Locale
   */
  public static Locale fromString(final String string) {
    if (string == null) {
      return null;
    } else {
      try {
        /*
         * prefer https://en.wikipedia.org/wiki/IETF_language_tag (only Locale.Builder
         * throws exception for illegal format)
         */
        return new Locale.Builder().setLanguageTag(string).build();
      } catch (final IllformedLocaleException ex) {
        // use fallback
        return LocaleUtils.toLocale(string);
      }
    }
  }
}
