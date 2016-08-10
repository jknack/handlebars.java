/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import java.util.IllformedLocaleException;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

/**
 * Utility methods to resolve {@link Locale}s from Strings.
 *
 */
public final class Locales {

  /**
   * Not allowed.
   */
  private Locales() {
  }

  /**
   * <p>Converts {@link String}s to {@link Locale}s. Supports both
   * <a href="https://en.wikipedia.org/wiki/IETF_language_tag">IETF language tags</a>
   * using hyphens (e.g. &quot;de-AT&quot;) and the Java format using underscores
   * (e.g. &quot;de_AT&quot;).</p>
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
         *  prefer https://en.wikipedia.org/wiki/IETF_language_tag
         *  (only Locale.Builder throws exception for illegal format)
         */
        return new Locale.Builder().setLanguageTag(string).build();
      } catch (final IllformedLocaleException ex) {
        // use fallback
        return LocaleUtils.toLocale(string);
      }
    }
  }

}
