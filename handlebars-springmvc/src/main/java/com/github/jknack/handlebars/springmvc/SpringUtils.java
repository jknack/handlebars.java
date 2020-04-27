/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.springmvc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.ApplicationContext;

import com.github.jknack.handlebars.helper.I18nSource;

/**
 * @author OhChang Kwon(ohchang.kwon@navercorp.com)
 */
public final class SpringUtils {
  /** private constructor for Util class. */
  private SpringUtils() {
  }

  /**
   * Creates a new i18n source.
   *
   * @param context The application context.
   * @return A new i18n source.
   */
  public static I18nSource createI18nSource(final ApplicationContext context) {
    return new I18nSource() {
      @Override
      public String message(final String key, final Locale locale, final Object... args) {
        return context.getMessage(key, args, locale);
      }

      @Override
      public String[] keys(final String basename, final Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(basename, locale);
        Enumeration<String> keys = bundle.getKeys();
        List<String> result = new ArrayList<>();
        while (keys.hasMoreElements()) {
          String key = keys.nextElement();
          result.add(key);
        }
        return result.toArray(new String[0]);
      }
    };
  }
}
