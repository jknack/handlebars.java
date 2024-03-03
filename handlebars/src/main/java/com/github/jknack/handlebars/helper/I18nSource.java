/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import java.util.Locale;

/**
 * Resolve message keys and message for internationalization. See: {@link
 * I18nHelper#setSource(I18nSource)}.
 *
 * @author edgar
 * @since 1.2.1
 */
public interface I18nSource {

  /**
   * List all the message's keys for the given locale.
   *
   * @param locale The current locale.
   * @param baseName The base name.
   * @return All the message's keys.
   */
  String[] keys(String baseName, Locale locale);

  /**
   * Try to resolve the message under the given key.
   *
   * @param key The message's key.
   * @param locale The current locale.
   * @param args The message arguments.
   * @return The message, <code>null</code> or a default message. It depends on the implementation.
   */
  String message(String key, Locale locale, Object... args);
}
