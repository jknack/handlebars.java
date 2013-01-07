/**
 * Copyright (c) 2012 Edgar Espina
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
package com.github.jknack.handlebars;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;
import humanize.Humanize;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

/**
 * Handlebars helper for the Humanize library.
 *
 * @author edgar.espina
 * @since 0.5.0
 */
public enum HumanizeHelper implements Helper<Object> {

  /**
   * <p>
   * Converts a given number to a string preceded by the corresponding binary
   * International System of Units (SI) prefix.
   * </p>
   *
   * <pre>
   * {{binaryPrefix number [locale="default"]}}
   * </pre>
   *
   * @see Humanize#binaryPrefix(Number, Locale)
   */
  binaryPrefix {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Number, "found '%s', expected: 'number'", value);
      return Humanize.binaryPrefix((Number) value, resolveLocale(options));
    }
  },

  /**
   * <p>
   * Makes a phrase camel case. Spaces and underscores will be removed.
   * </p>
   *
   * <pre>
   * {{camelize stringValue [capFirst=false]}}
   * </pre>
   *
   * @see Humanize#camelize(String, boolean)
   */
  camelize {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      Boolean capFirst = options.hash("capFirst", true);
      return Humanize.camelize((String) value, capFirst);
    }
  },

  /**
   * <p>
   * Makes the first letter uppercase and the rest lowercase.
   * </p>
   *
   * <pre>
   * {{capitalize word}}
   * </pre>
   *
   * @see Humanize#capitalize(String)
   */
  capitalize {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      return Humanize.capitalize((String) value);
    }
  },

  /**
   * <p>
   * Converts a camel case string into a human-readable name.
   * </p>
   *
   * <pre>
   * {{decamelize string [replacement=" "]}}
   * </pre>
   *
   * @see Humanize#decamelize(String, String)
   */
  decamelize {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      String replacement = options.hash("replacement", " ");
      return Humanize.decamelize((String) value, replacement);
    }
  },

  /**
   * <p>
   * Smartly formats the given number as a monetary amount.
   * </p>
   *
   * <pre>
   * {{formatCurrency string [locale="default"]}}
   * </pre>
   *
   * @see Humanize#formatCurrency(Number, java.util.Locale)
   */
  formatCurrency {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Number, "found '%s', expected: 'number'", value);
      return Humanize.formatCurrency((Number) value, resolveLocale(options));
    }
  },

  /**
   * <p>
   * Formats the given ratio as a percentage.
   * </p>
   *
   * <pre>
   * {{formatPercent string [locale="default"]}}
   * </pre>
   *
   * @see Humanize#formatPercent(Number)
   */
  formatPercent {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Number, "found '%s', expected: 'number'", value);
      return Humanize.formatPercent((Number) value, resolveLocale(options));
    }
  },

  /**
   * <p>
   * Converts a given number to a string preceded by the corresponding decimal
   * multiplicative prefix.
   * </p>
   *
   * <pre>
   * {{metricPrefix string [locale="default"]}}
   * </pre>
   *
   * @see Humanize#metricPrefix(Number, Locale)
   */
  metricPrefix {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Number, "found '%s', expected: 'number'", value);
      return Humanize.metricPrefix((Number) value, resolveLocale(options));
    }
  },

  /**
   * For dates that are the current day or within one day, return 'today',
   * 'tomorrow' or 'yesterday', as appropriate. Otherwise, returns a string
   * formatted according to a locale sensitive DateFormat.
   *
   * <pre>
   * {{naturalDay date}}
   * </pre>
   *
   * @see Humanize#naturalDay(Date)
   */
  naturalDay {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Date, "found '%s', expected: 'date'", value);
      return Humanize.naturalDay((Date) value);
    }
  },

  /**
   * Computes both past and future relative dates.
   * <p>
   * E.g. 'one day ago', 'one day from now', '10 years ago', '3 minutes from
   * now', 'right now' and so on.
   * </p>
   *
   * <pre>
   * {{naturalTime date [locale="default"]}}
   * </pre>
   *
   * @see Humanize#naturalTime(Date, Locale)
   */
  naturalTime {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Date, "found '%s', expected: 'date'", value);
      return Humanize.naturalTime((Date) value, resolveLocale(options));
    }
  },

  /**
   * Converts a number to its ordinal as a string.
   * <p>
   * E.g. 1 becomes '1st', 2 becomes '2nd', 3 becomes '3rd', etc.
   * </p>
   *
   * <pre>
   * {{ordinal number [locale="default"]}}
   * </pre>
   *
   * @see Humanize#ordinal(Number, Locale)
   */
  ordinal {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Number, "found '%s', expected: 'number'", value);
      return Humanize.ordinal((Number) value, resolveLocale(options));
    }
  },

  /**
   * <p>
   * Constructs a message with pluralization logic from the given template.
   * </p>
   * <h5>Examples:</h5>
   *
   * <pre>
   * MessageFormat msg =
   *    pluralize(&quot;There {0} on {1}.::are no files::is one file::are {0}
   *        files&quot;);
   *
   * // == &quot;There are no files on disk.&quot;
   * msg.render(0, &quot;disk&quot;);
   *
   * // == &quot;There is one file on disk.&quot;
   * msg.render(1, &quot;disk&quot;);
   *
   * // == &quot;There is one file on disk.&quot;
   * msg.render(1000, &quot;disk&quot;);
   * </pre>
   *
   * <pre>
   * MessageFormat msg = pluralize(&quot;nothing::one thing::{0} things&quot;);
   *
   * msg.render(-1); // == &quot;nothing&quot;
   * msg.render(0); // == &quot;nothing&quot;
   * msg.render(1); // == &quot;one thing&quot;
   * msg.render(2); // == &quot;2 things&quot;
   * </pre>
   *
   * <pre>
   * MessageFormat msg = pluralize(&quot;one thing::{0} things&quot;);
   *
   * msg.render(-1); // == &quot;-1 things&quot;
   * msg.render(0); // == &quot;0 things&quot;
   * msg.render(1); // == &quot;one thing&quot;
   * msg.render(2); // == &quot;2 things&quot;
   * </pre>
   *
   * <pre>
   * {{pluralize "pattern" arg0, arg1, ..., argn [locale="default"]}}
   * </pre>
   *
   * @see Humanize#pluralize(String, Locale)
   */
  pluralize {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      return Humanize.pluralize((String) value, resolveLocale(options))
          .render(options.params);
    }
  },

  /**
   * <p>
   * Transforms a text into a representation suitable to be used in an URL.
   * </p>
   * <table border="0" cellspacing="0" cellpadding="3" width="100%">
   * <tr>
   * <th class="colFirst">Input</th>
   * <th class="colLast">Output</th>
   * </tr>
   * <tr>
   * <td>"J'étudie le français"</td>
   * <td>"jetudie-le-francais"</td>
   * </tr>
   * <tr>
   * <td>"Lo siento, no hablo español."</td>
   * <td>"lo-siento-no-hablo-espanol"</td>
   * </tr>
   * </table>
   *
   * <pre>
   * {{slugify string}}
   * </pre>
   *
   * @see Humanize#slugify(String)
   */
  slugify {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      return Humanize.slugify((String) value);
    }
  },

  /**
   * <p>
   * Converts a big number to a friendly text representation. Accepts values
   * ranging from thousands to googols. Uses BigDecimal.
   * </p>
   *
   * <pre>
   * {{spellNumber number [locale="default"]}}
   * </pre>
   *
   * @see Humanize#spellBigNumber(Number, Locale)
   */
  spellNumber {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Number, "found '%s', expected: 'number'", value);
      return Humanize.spellBigNumber((Number) value, resolveLocale(options));
    }
  },

  /**
   * <p>
   * For decimal digits [0-9], returns the number spelled out. Otherwise,
   * returns the number as string.
   * </p>
   *
   * <pre>
   * {{spellDigit digit [locale="default"]}}
   * </pre>
   *
   * @see Humanize#spellDigit(Number, Locale)
   */
  spellDigit {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Number, "found '%s', expected: 'number'", value);
      return Humanize.spellDigit((Number) value, resolveLocale(options));
    }
  },

  /**
   * <p>
   * Capitalize all the words, and replace some characters in the string to
   * create a nice looking title.
   * </p>
   *
   * <pre>
   * {{titleize string}}
   * </pre>
   *
   * @see Humanize#titleize(String)
   */
  titleize {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      return Humanize.titleize((String) value);
    }
  },

  /**
   * <p>
   * Strips diacritic marks.
   * </p>
   *
   * <pre>
   * {{transliterate string}}
   * </pre>
   *
   * @see Humanize#transliterate(String)
   */
  transliterate {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      return Humanize.transliterate((String) value);
    }
  },

  /**
   * <p>
   * Makes a phrase underscored instead of spaced.
   * </p>
   *
   * <pre>
   * {{underscore string}}
   * </pre>
   *
   * @see Humanize#underscore(String)
   */
  underscore {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      return Humanize.underscore((String) value);
    }
  },

  /**
   * <p>
   * Truncate a string to the closest word boundary after a number of
   * characters.
   * </p>
   *
   * <pre>
   * {{wordWrap string length}}
   * </pre>
   *
   * @see Humanize#wordWrap(String, int)
   */
  wordWrap {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected: 'string'", value);
      Number length = options.param(0, null);
      notNull(length, "found 'null', expected 'word wrap length'");
      isTrue(length.intValue() > 0, "found '%s', expected 'a positive number'",
          length);
      return Humanize.wordWrap((String) value, length.intValue());
    }
  };

  /**
   * Resolve a locale.
   *
   * @param options The helper's options.
   * @return A locale.
   */
  protected static Locale resolveLocale(final Options options) {
    String locale = options.hash("locale", Locale.getDefault().toString());
    return LocaleUtils.toLocale(locale);
  }

  /**
   * Register all the humanize helpers.
   *
   * @param handlebars The helper's owner.
   */
  public static void register(final Handlebars handlebars) {
    notNull(handlebars, "A handlebars object is required.");
    for (HumanizeHelper helper : values()) {
      handlebars.registerHelper(helper.name(), helper);
    }
  }
}
