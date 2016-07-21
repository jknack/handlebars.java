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

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.internal.Locales;

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
   *
   * <h3>messages.properties:</h3>
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
    public Object apply(final String key, final Options options) throws IOException {
      notEmpty(key, "found: '%s', expected 'bundle's key'", key);
      Locale locale = Locales
          .fromString((String) options.hash("locale", defaultLocale.toString()));
      String baseName = options.hash("bundle", defaultBundle);
      ClassLoader classLoader = options.hash("classLoader", getClass().getClassLoader());
      I18nSource localSource = source == null
          ? new DefI18nSource(baseName, locale, classLoader) : source;

      return localSource.message(key, locale, options.params);
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
    private final Pattern pattern = Pattern.compile("\\{(\\d+)\\}");

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
     *  {{i18nJs [locale] [bundle=messages] [wrap=true]}}
     * </pre>
     *
     * If locale argument is present it will translate that locale to JavaScript. Otherwise, the
     * default locale.
     *
     * Use wrap=true for wrapping the code with a script tag.
     *
     * @param localeName The locale's name. Optional.
     * @param options The helper's options. Not null.
     * @return JavaScript code from {@link ResourceBundle}.
     * @throws IOException If bundle wasn't resolve.
     */
    @Override
    public Object apply(final String localeName, final Options options) throws IOException {
      Locale locale = Locales.fromString(defaultIfEmpty(localeName, defaultLocale.toString()));
      String baseName = options.hash("bundle", defaultBundle);
      ClassLoader classLoader = options.hash("classLoader", getClass().getClassLoader());
      I18nSource localSource = source == null
          ? new DefI18nSource(baseName, locale, classLoader) : source;
      StringBuilder buffer = new StringBuilder();
      Boolean wrap = options.hash("wrap", true);
      if (wrap) {
        buffer.append("<script type='text/javascript'>\n");
      }
      buffer.append("  /* ").append(locale.getDisplayName()).append(" */\n");
      buffer.append("  I18n.translations = I18n.translations || {};\n");
      buffer.append("  I18n.translations['").append(locale.toString()).append("'] = {\n");
      StringBuilder body = new StringBuilder();
      String separator = ",\n";
      String[] keys = localSource.keys(baseName, locale);
      for (String key : keys) {
        String message = message(localSource.message(key, locale));
        body.append("    \"").append(key).append("\": ");
        body.append("\"").append(message).append("\"").append(separator);
      }
      if (body.length() > 0) {
        body.setLength(body.length() - separator.length());
        buffer.append(body);
      }
      buffer.append("\n  };\n");
      if (wrap) {
        buffer.append("</script>\n");
      }
      return new Handlebars.SafeString(buffer);
    }

    /**
     * Convert expression <code>{0}</code> into <code>{{arg0}}</code> and escape EcmaScript
     * characters.
     *
     * @param message The candidate message.
     * @return A valid I18n message.
     */
    private String message(final String message) {
      CharSequence escapedMessage = Handlebars.Utils.escapeExpression(message);
      Matcher matcher = pattern.matcher(escapedMessage);
      StringBuffer result = new StringBuffer();
      while (matcher.find()) {
        matcher.appendReplacement(result, "{{arg" + matcher.group(1) + "}}");
      }
      matcher.appendTail(result);
      return result.toString();
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

  /** The message source to use. */
  protected I18nSource source;

  /**
   * Set the message source.
   *
   * NotThreadSafe Make sure to call this method ONCE at start time.
   *
   * @param source The message source. Required.
   */
  public void setSource(final I18nSource source) {
    this.source = notNull(source, "The i18n source is required.");
  }

  /**
   * Set the default bundle's name. Default is: messages and this method let you override the
   * default bundle's name to something else.
   *
   * NotThreadSafe Make sure to call this method ONCE at start time.
   *
   * @param bundle The default's bundle name. Required.
   */
  public void setDefaultBundle(final String bundle) {
    this.defaultBundle = notEmpty(bundle, "A bundle's name is required.");
  }

  /**
   * Set the default locale. Default is system dependent and this method let you override the
   * default bundle's name to something else.
   *
   * NotThreadSafe Make sure to call this method ONCE at start time.
   *
   * @param locale The default locale name. Required.
   */
  public void setDefaultLocale(final Locale locale) {
    this.defaultLocale = notNull(locale, "A locale is required.");
  }

}

/** Default implementation of I18nSource. */
class DefI18nSource implements I18nSource {

  /** The resource bundle. */
  private ResourceBundle bundle;

  /**
   * Creates a new {@link DefI18nSource}.
   *
   * @param baseName The base name.
   * @param locale The locale.
   * @param classLoader The classloader.
   */
  public DefI18nSource(final String baseName, final Locale locale, final ClassLoader classLoader) {
    bundle = ResourceBundle.getBundle(baseName, locale, classLoader);
  }

  @Override
  public String[] keys(final String basename, final Locale locale) {
    Enumeration<String> keys = bundle.getKeys();
    List<String> result = new ArrayList<String>();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      result.add(key);
    }
    return result.toArray(new String[result.size()]);
  }

  @Override
  public String message(final String key, final Locale locale, final Object... args) {
    isTrue(bundle.containsKey(key), "no message found: '%s' for locale '%s'.", key, locale);
    String message = bundle.getString(key);
    if (args.length == 0) {
      return message;
    }
    MessageFormat format = new MessageFormat(message, locale);
    return format.format(args);
  }

};
