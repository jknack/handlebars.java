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
package com.github.jknack.handlebars.helper;

import java.util.Locale;

/**
 * Resolve message keys and message for internationalization. See:
 * {@link I18nHelper#setSource(I18nSource)}.
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
