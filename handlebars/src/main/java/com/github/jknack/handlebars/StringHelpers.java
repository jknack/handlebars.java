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
package com.github.jknack.handlebars;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Commons string function helpers.
 *
 * @author edgar.espina
 * @since 0.2.2
 */
public enum StringHelpers implements Helper<Object> {

  /**
   * Capitalizes the first character of the value.
   * For example:
   *
   * <pre>
   * {{capFirst value}}
   * </pre>
   *
   * If value is "handlebars.java", the output will be "Handlebars.java".
   */
  capitalizeFirst {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected 'string'", value);
      return StringUtils.capitalize(value.toString());
    }

  },

  /**
   * Centers the value in a field of a given width.
   * For example:
   *
   * <pre>
   * {{center value size=19 [pad="char"] }}
   * </pre>
   *
   * If value is "Handlebars.java", the output will be "  Handlebars.java  ".
   */
  center {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected 'string'", value);
      Integer size = options.hash("size");
      notNull(size, "found 'null', expected 'size'");
      String pad = options.hash("pad", " ");
      return StringUtils.center(value.toString(), size, pad);
    }
  },

  /**
   * Removes all values of arg from the given string.
   * For example:
   *
   * <pre>
   * {{cut value [" "]}}
   * </pre>
   *
   * If value is "String with spaces", the output will be "Stringwithspaces".
   */
  cut {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected 'string'", value);
      String strip = options.param(0, " ");
      return value.toString().replace(strip, "");
    }
  },

  /**
   * If value evaluates to False, uses the given default. Otherwise, uses the
   * value.
   * For example:
   *
   * <pre>
   * {{defaultIfEmpty value ["nothing"] }}
   * If value is "" (the empty string), the output will be nothing.
   * </pre>
   */
  defaultIfEmpty {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      if (Handlebars.Utils.isEmpty(value)) {
        return options.param(0, "");
      }
      return String.valueOf(value);
    }
  },

  /**
   * Joins an array, iterator or an iterable with a string.
   * For example:
   *
   * <pre>
   * {{join value " // " [prefix=""] [suffix=""]}}
   * </pre>
   *
   * <p>
   * If value is the list ['a', 'b', 'c'], the output will be the string "a // b // c".
   * </p>
   * Or:
   *
   * <pre>
   * {{join "a" "b" "c" " // " [prefix=""] [suffix=""]}}
   * <p>Join the "a", "b", "c", the output will be the string "a // b // c".</p>
   * </pre>
   */
  join {
    @SuppressWarnings("rawtypes")
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      int separatorIdx = options.params.length - 1;
      Object separator = options.param(separatorIdx, null);
      notNull(separator, "found 'null', expected 'separator' at param[%s]", separatorIdx);
      isTrue(separator instanceof String,
          "found '%s', expected 'separator' at param[%s]", separator, separatorIdx);
      String prefix = options.hash("prefix", "");
      String suffix = options.hash("suffix", "");
      if (context instanceof Iterable) {
        return prefix + StringUtils.join((Iterable) context, (String) separator) + suffix;
      }
      if (context instanceof Iterator) {
        return prefix + StringUtils.join((Iterator) context, (String) separator) + suffix;
      }
      if (context.getClass().isArray()) {
        return prefix + StringUtils.join((Object[]) context, (String) separator) + suffix;
      }
      // join everything as single values
      Object[] values = new Object[options.params.length];
      System.arraycopy(options.params, 0, values, 1, separatorIdx);
      values[0] = context;
      return prefix + StringUtils.join(values, (String) separator) + suffix;
    }
  },

  /**
   * Left-aligns the value in a field of a given width.
   * Argument: field size
   * For example:
   *
   * <pre>
   * {{ljust value 20 [pad=" "] }}
   * </pre>
   *
   * If value is Handlebars.java, the output will be "Handlebars.java     ".
   */
  ljust {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected 'string'", value);
      Integer size = options.hash("size");
      notNull(size, "found 'null', expected 'size'");
      String pad = options.hash("pad", " ");
      return StringUtils.rightPad(value.toString(), size, pad);
    }
  },

  /**
   * Right-aligns the value in a field of a given width.
   * Argument: field size
   * For example:
   *
   * <pre>
   * {{rjust value 20 [pad=" "] }}
   * </pre>
   *
   * If value is Handlebars.java, the output will be "     Handlebars.java".
   */
  rjust {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected 'string'", value);
      Integer size = options.hash("size");
      notNull(size, "found 'null', expected 'size'");
      String pad = options.hash("pad", " ");
      return StringUtils.leftPad(value.toString(), size, pad);
    }
  },

  /**
   * Converts a string into all lowercase.
   * For example:
   *
   * <pre>
   * {{lower value}}
   * </pre>
   *
   * If value is 'Still MAD At Yoko', the output will be 'still mad at yoko'.
   */
  lower {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected 'string'", value);
      return ((String) value).toLowerCase();
    }
  },

  /**
   * Converts a string into all lowercase.
   * For example:
   *
   * <pre>
   * {{upper value}}
   * </pre>
   *
   * If value is 'Hello', the output will be 'HELLO'.
   */
  upper {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof String, "found '%s', expected 'string'", value);
      return ((String) value).toUpperCase();
    }
  },

  /**
   * Converts to lowercase, removes non-word characters (alphanumerics and
   * underscores) and converts spaces to hyphens. Also strips leading and
   * trailing whitespace.
   * For example:
   *
   * <pre>
   * {{slugify value}}
   * </pre>
   *
   * If value is "Joel is a slug", the output will be "joel-is-a-slug".
   */
  slugify {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      isTrue(context instanceof String, "found '%s', expected 'string'",
          context);
      String value = StringUtils.strip((String) context);
      StringBuilder buffer = new StringBuilder(value.length());
      for (int i = 0; i < value.length(); i++) {
        char ch = value.charAt(i);
        if (Character.isLetter(ch)) {
          buffer.append(Character.toLowerCase(ch));
        }
        if (Character.isWhitespace(ch)) {
          buffer.append('-');
        }
      }
      return buffer.toString();
    }
  },

  /**
   * Formats the variable according to the argument, a string formatting
   * specifier.
   * For example:
   *
   * <pre>
   * {{stringFormat string param0 param1 ... paramN}}
   * </pre>
   *
   * If value is "Hello %s" "handlebars.java", the output will be
   * "Hello handlebars.java".
   *
   * @see String#format(String, Object...)
   */
  stringFormat {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      isTrue(context instanceof String, "found '%s', expected 'string'",
          context);
      String format = (String) context;
      return String.format(format, options.params);
    }

  },

  /**
   * Strips all [X]HTML tags.
   * For example:
   *
   * <pre>
   * {{stripTags value}}
   * </pre>
   */
  stripTags {

    /**
     * The HTML tag pattern.
     */
    private final Pattern pattern = Pattern
        .compile("\\<[^>]*>", Pattern.DOTALL);

    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      isTrue(context instanceof String, "found '%s', expected 'string'",
          context);
      Matcher matcher = pattern.matcher((String) context);
      return matcher.replaceAll("");
    }

  },

  /**
   * Capitalizes all the whitespace separated words in a String.
   * For example:
   *
   * <pre>
   * {{ capitalize value [fully=false]}}
   * </pre>
   *
   * If value is "my first post", the output will be "My First Post".
   */
  capitalize {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      isTrue(context instanceof String, "found '%s', expected 'string'",
          context);
      Boolean fully = options.hash("fully", false);
      String value = (String) context;
      return fully
          ? WordUtils.capitalizeFully(value)
          : WordUtils.capitalize(value);
    }
  },

  /**
   * Truncates a string if it is longer than the specified number of characters.
   * Truncated strings will end with a translatable ellipsis sequence ("...").
   * Argument: Number of characters to truncate to
   * For example:
   *
   * <pre>
   * {{abbreviate value 13 }}
   * </pre>
   *
   * If value is "Handlebars rocks", the output will be "Handlebars...".
   */
  abbreviate {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      isTrue(context instanceof String, "found '%s', expected 'string'",
          context);
      Integer width = options.param(0, null);
      notNull(width, "found 'null', expected 'width'");
      return StringUtils.abbreviate((String) context, width);
    }
  },

  /**
   * Wraps words at specified line length.
   * Argument: number of characters at which to wrap the text
   * For example:
   *
   * <pre>
   * {{ wordWrap value 5 }}
   * </pre>
   *
   * If value is Joel is a slug, the output would be:
   *
   * <pre>
   * Joel
   * is a
   * slug
   * </pre>
   */
  wordWrap {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      isTrue(context instanceof String, "found '%s', expected 'string'",
          context);
      Integer length = options.param(0, null);
      notNull(length, "found 'null', expected 'length'");
      return WordUtils.wrap((String) context, length);
    }

  },

  /**
   * Maps values for true, false and (optionally) null, to the strings "yes",
   * "no", "maybe".
   * For example:
   *
   * <pre>
   * {{yesno value [yes="yes"] [no="no"] maybe=["maybe"] }}
   * </pre>
   */
  yesno {
    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      if (value == null) {
        return options.hash("maybe", "maybe");
      }
      isTrue(value instanceof Boolean, "found '%s', expected 'boolean'",
          value);
      if (Boolean.TRUE.equals(value)) {
        return options.hash("yes", "yes");
      }
      return options.hash("no", "no");
    }
  },

  /**
   * <p>
   * Usage:
   * </p>
   *
   * <pre>
   *    {{dateFormat date ["format"]}}
   * </pre>
   *
   * Format parameters is one of:
   * <ul>
   * <li>"full": full date format. For example: Tuesday, June 19, 2012</li>
   * <li>"long": long date format. For example: June 19, 2012</li>
   * <li>"medium": medium date format. For example: Jun 19, 2012</li>
   * <li>"short": short date format. For example: 6/19/12</li>
   * <li>"pattern": a date pattern.</li>
   * </ul>
   * Otherwise, the default formatter will be used.
   */
  dateFormat {
    /**
     * The default date styles.
     */
    @SuppressWarnings("serial")
    private Map<String, Integer> styles = new HashMap<String, Integer>()
    {
      {
        put("full", DateFormat.FULL);
        put("long", DateFormat.LONG);
        put("medium", DateFormat.MEDIUM);
        put("short", DateFormat.SHORT);
      }
    };

    @Override
    public CharSequence apply(final Object value, final Options options)
        throws IOException {
      isTrue(value instanceof Date, "found '%s', expected 'date'", value);

      Date date = (Date) value;
      final DateFormat dateFormat;
      Object pattern = options.param(0, "medium");
      String localeStr = options.param(1, Locale.getDefault().toString());
      Locale locale = LocaleUtils.toLocale(localeStr);
      Integer style = styles.get(pattern);
      if (style == null) {
        dateFormat = new SimpleDateFormat(pattern.toString(), locale);
      } else {
        dateFormat = DateFormat.getDateInstance(style, locale);
      }
      return dateFormat.format(date);
    }

  };

  /**
   * Register the helper in a handlebars instance.
   *
   * @param handlebars A handlebars object. Required.
   */
  public void registerHelper(final Handlebars handlebars) {
    notNull(handlebars, "The handlebars is required.");
    handlebars.registerHelper(name(), this);
  }

  /**
   * Register all the text helpers.
   *
   * @param handlebars The helper's owner. Required.
   */
  public static void register(final Handlebars handlebars) {
    notNull(handlebars, "A handlebars object is required.");
    StringHelpers[] helpers = values();
    for (StringHelpers helper : helpers) {
      helper.registerHelper(handlebars);
    }
  }
}
