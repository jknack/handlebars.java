/**
 * Copyright (c) 2012-2013 Edgar Espina
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

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Implementation of i18n helper for Java and JavaScript.
 * <p>
 * The Java implementation use {@link ResourceBundle}.
 * </p>
 * <p>
 * The JavaScript version use the <a href="https://github.com/fnando/i18n-js">I18n</a> library.
 * {@link ResourceBundle} are converted to JavaScript code.
 * </p>
 *
 * @author edgar.espina
 * @since 0.9.0
 * @see ResourceBundle
 */
public enum I18nHelper implements Helper<String> {

  /**
   * <p>
   * A helper built on top of {@link ResourceBundle}. A {@link ResourceBundle} is the most well
   * known mechanism for internationalization (i18n).
   * </p>
   * <p>
   * <h3>messages.properties:</h3>
   * </p>
   *
   * <pre>
   *  hello=Hola
   * </pre>
   *
   * <h3>Basic Usage:</h3>
   *
   * <pre>
   *  {{i18n "hello"}}
   * </pre>
   *
   * <p>
   * Will result in: <code>Hola</code>
   * </p>
   * <h3>Using a locale:</h3>
   *
   * <pre>
   *  {{i18n "hello" locale="es_AR"}}
   * </pre>
   *
   * <h3>Using a different bundle:</h3>
   *
   * <pre>
   *  {{i18n "hello" bundle="myMessages"}}
   * </pre>
   *
   * <h3>Using a message format:</h3>
   *
   * <pre>
   *  hello=Hola {0}!
   * </pre>
   *
   * <pre>
   *  {{i18n "hello" "Handlebars.java"}}
   * </pre>
   *
   * @author edgar.espina
   * @since 0.9.0
   * @see ResourceBundle
   */
  i18n {
    /**
     * <p>
     * A helper built on top of {@link ResourceBundle}. A {@link ResourceBundle} is the most well
     * known mechanism for internationalization (i18n).
     * </p>
     * <p>
     * <h3>messages.properties:</h3>
     * </p>
     *
     * <pre>
     *  hello=Hola
     * </pre>
     *
     * <h3>Basic Usage:</h3>
     *
     * <pre>
     *  {{i18n "hello"}}
     * </pre>
     *
     * <p>
     * Will result in: <code>Hola</code>
     * </p>
     * <h3>Using a locale:</h3>
     *
     * <pre>
     * {{i18n "hello" locale="es_AR"}}
     * </pre>
     *
     * <h3>Using a different bundle:</h3>
     *
     * <pre>
     *  {{i18n "hello" bundle="myMessages"}}
     * </pre>
     *
     * <h3>Using a message format</h3>
     *
     * <pre>
     *  hello=Hola {0}!
     * </pre>
     *
     * <pre>
     *  {{i18n "hello" "Handlebars.java"}}
     * </pre>
     *
     * @param key The bundle's key. Required.
     * @param options The helper's options. Not null.
     * @return An i18n message.
     * @throws IOException If the bundle wasn't resolve.
     */
    @Override
    public CharSequence apply(final String key, final Options options) throws IOException {
      notEmpty(key, "found: '%s', expected 'bundle's key'", key);
      Locale locale = LocaleUtils
          .toLocale((String) options.hash("locale", defaultLocale.toString()));
      String baseName = options.hash("bundle", defaultBundle);
      ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
      isTrue(bundle.containsKey(key), "no message found: '%s' for locale '%s' using '%s'.", key,
          locale, baseName);
      String message = bundle.getString(key);
      if (options.params.length == 0) {
        return message;
      }
      MessageFormat format = new MessageFormat(message, locale);
      return format.format(options.params);
    }
  },

  /**
   * <p>
   * Translate a {@link ResourceBundle} into JavaScript code. The generated code assume you added
   * the <a href="https://github.com/fnando/i18n-js">I18n</a>
   * </p>
   * <p>
   * It converts message patterns like: <code>Hi {0}</code> into <code>Hi {{arg0}}</code>. This make
   * possible to the I18n JS library to interpolate variables.
   * </p>
   * <p>
   * Note: make sure you include <a href="https://github.com/fnando/i18n-js">I18n</a> in your
   * application. Otherwise, the generated code will fail.
   * </p>
   * <p>
   * Usage:
   * </p>
   *
   * <pre>
   *  {{i18nJs locale?}}
   * </pre>
   *
   * If locale argument is present it will translate that locale to JavaScript. Otherwise, the
   * default locale.
   */
  i18nJs {

    /**
     * The message format pattern.
     */
    private final Pattern pattern = Pattern.compile("\\{(\\d+.*)\\}");

    /**
     * <p>
     * Translate a {@link ResourceBundle} into JavaScript code. The generated code assume you added
     * the <a href="https://github.com/fnando/i18n-js">I18n</a>
     * </p>
     * <p>
     * It converts message patterns like: <code>Hi {0}</code> into <code>Hi {{arg0}}</code>. This
     * make possible to the I18n JS library to interpolate variables.
     * </p>
     * <p>
     * Note: make sure you include <a href="https://github.com/fnando/i18n-js">I18n</a> in your
     * application. Otherwise, the generated code will fail.
     * </p>
     * <p>
     * Usage:
     * </p>
     *
     * <pre>
     *  {{i18nJs [locale] [bundle=messages]}}
     * </pre>
     *
     * If locale argument is present it will translate that locale to JavaScript. Otherwise, the
     * default locale.
     *
     * @param localeName The locale's name. Optional.
     * @param options The helper's options. Not null.
     * @return JavaScript code from {@link ResourceBundle}.
     * @throws IOException If bundle wasn't resolve.
     */
    @Override
    public CharSequence apply(final String localeName, final Options options) throws IOException {
      Locale locale = LocaleUtils.toLocale(defaultString(localeName, defaultLocale.toString()));
      String baseName = options.hash("bundle", defaultBundle);
      ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
      StringBuilder buffer = new StringBuilder();
      buffer.append("<script type='text/javascript'>\n");
      buffer.append("  I18n.defaultLocale = '").append(defaultLocale).append("';\n");
      buffer.append("  I18n.locale = '").append(defaultLocale).append("';\n");
      buffer.append("  I18n.translations = I18n.translations || {};\n");
      buffer.append("  // ").append(locale.getDisplayName()).append("\n");
      buffer.append("  I18n.translations['").append(locale.toString()).append("'] = {\n");
      StringBuilder body = new StringBuilder();
      String separator = ",\n";
      Enumeration<String> keys = bundle.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        String message = message(bundle.getString(key));
        body.append("    \"").append(key).append("\": ");
        body.append("\"").append(message).append("\"").append(separator);
      }
      if (body.length() > 0) {
        body.setLength(body.length() - separator.length());
        buffer.append(body);
      }
      return new Handlebars.SafeString(buffer.append("\n  };\n").append("</script>\n"));
    }

    /**
     * Convert expression <code>{0}</code> into <code>{{arg0}}</code> and escape EcmaScript
     * characters.
     *
     * @param message The candidate message.
     * @return A valid I18n message.
     */
    private String message(final String message) {
      Matcher matcher = pattern.matcher(message);
      StringBuffer result = new StringBuffer();
      while (matcher.find()) {
        matcher.appendReplacement(result, "{{arg" + matcher.group(1) + "}}");
      }
      matcher.appendTail(result);
      return StringEscapeUtils.escapeEcmaScript(result.toString());
    }
  };

  /**
   * The default locale. Required.
   */
  protected Locale defaultLocale = Locale.getDefault();

  /**
   * The default's bundle. Required.
   */
  protected String defaultBundle = "messages";

  /**
   * Register this helper.
   *
   * @param handlebars A handlebars object. Required.
   */
  public void registerHelper(final Handlebars handlebars) {
    notNull(handlebars, "The handlebars is required.");
    handlebars.registerHelper(name(), this);
  }

  /**
   * Register all the helpers.
   *
   * @param handlebars A handlebars object. Required.
   */
  public static void registerHelpers(final Handlebars handlebars) {
    for (I18nHelper helper : values()) {
      helper.registerHelper(handlebars);
    }
  }
}
