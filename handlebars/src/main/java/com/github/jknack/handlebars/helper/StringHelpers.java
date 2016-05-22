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

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.validIndex;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

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
    protected CharSequence safeApply(final Object value, final Options options) {
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
    protected CharSequence safeApply(final Object value, final Options options) {
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
    protected CharSequence safeApply(final Object value, final Options options) {
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
    public Object apply(final Object value, final Options options)
        throws IOException {
      if (Handlebars.Utils.isEmpty(value)) {
        return options.param(0, "");
      }
      return String.valueOf(value);
    }

    @Override
    protected CharSequence safeApply(final Object context, final Options options) {
      // Ignored
      return null;
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
   * Join the "a", "b", "c", the output will be the string "a // b // c".
   * </pre>
   */
  join {
    @Override
    public Object apply(final Object context, final Options options) {
      if (options.isFalsy(context)) {
        return "";
      }
      return safeApply(context, options);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected CharSequence safeApply(final Object context, final Options options) {
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
    protected CharSequence safeApply(final Object value, final Options options) {
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
    protected CharSequence safeApply(final Object value, final Options options) {
      Integer size = options.hash("size");
      notNull(size, "found 'null', expected 'size'");
      String pad = options.hash("pad", " ");
      return StringUtils.leftPad(value.toString(), size, pad);
    }
  },

  /**
   * Returns a new <code>CharSequence</code> that is a subsequence of this sequence.
   * The subsequence starts with the <code>char</code> value at the specified index and
   * ends with the <code>char</code> value at index <tt>end - 1</tt>
   * Argument: start offset
   *           end offset
   * For example:
   *
   * <pre>
   * {{substring value 11 }}
   * </pre>
   *
   * If value is Handlebars.java, the output will be "java".
   *
   * or
   *
   * <pre>
   * {{substring value 0 10 }}
   * </pre>
   *
   * If value is Handlebars.java, the output will be "Handlebars".
   */
  substring {
    @Override
    protected CharSequence safeApply(final Object value, final Options options) {
      validIndex(options.params, 0, "Required start offset: ");

      String str = value.toString();
      Integer start = options.param(0);
      Integer end = options.param(1, str.length());
      return str.subSequence(start, end);
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
    protected CharSequence safeApply(final Object value, final Options options) {
      return value.toString().toLowerCase();
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
    protected CharSequence safeApply(final Object value, final Options options) {
      return value.toString().toUpperCase();
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
    protected CharSequence safeApply(final Object context, final Options options) {
      String value = StringUtils.strip(context.toString());
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
    protected CharSequence safeApply(final Object format, final Options options) {
      return String.format(format.toString(), options.params);
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
    protected CharSequence safeApply(final Object value, final Options options) {
      Matcher matcher = pattern.matcher(value.toString());
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
    protected CharSequence safeApply(final Object value, final Options options) {
      Boolean fully = options.hash("fully", false);
      return fully
          ? WordUtils.capitalizeFully(value.toString())
          : WordUtils.capitalize(value.toString());
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
    protected CharSequence safeApply(final Object value, final Options options) {
      Integer width = options.param(0, null);
      notNull(width, "found 'null', expected 'width'");
      return StringUtils.abbreviate(value.toString(), width);
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
    protected CharSequence safeApply(final Object value, final Options options) {
      Integer length = options.param(0, null);
      notNull(length, "found 'null', expected 'length'");
      return WordUtils.wrap(value.toString(), length);
    }

  },

  /**
   * Replaces each substring of this string that matches the literal target
   * sequence with the specified literal replacement sequence.
   * For example:
   *
   * <pre>
   * {{ replace value "..." "rocks" }}
   * </pre>
   *
   * If value is "Handlebars ...", the output will be "Handlebars rocks".
   *
   */
  replace {
    @Override
    public CharSequence safeApply(final Object value, final Options options) {
      String target = (String) options.param(0, null);
      String replacement = (String) options.param(1, null);
      return value.toString().replace(target, replacement);
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
    public Object apply(final Object value, final Options options)
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

    @Override
    protected CharSequence safeApply(final Object context, final Options options) {
      return null;
    }

  },

  /**
   * <p>
   * Usage:
   * </p>
   *
   * <pre>
   *    {{dateFormat date ["format"] [format="format"][tz=timeZone|timeZoneId]}}
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
   * The format option can be specified as a parameter or hash (a.k.a named parameter).
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
    protected CharSequence safeApply(final Object value, final Options options) {
      isTrue(value instanceof Date, "found '%s', expected 'date'", value);
      Date date = (Date) value;
      final DateFormat dateFormat;
      Object pattern = options.param(0, options.hash("format", "medium"));
      String localeStr = options.param(1, Locale.getDefault().toString());
      Locale locale = LocaleUtils.toLocale(localeStr);
      Integer style = styles.get(pattern);
      if (style == null) {
        dateFormat = new SimpleDateFormat(pattern.toString(), locale);
      } else {
        dateFormat = DateFormat.getDateInstance(style, locale);
      }
      Object tz = options.hash("tz");
      if (tz != null) {
        final TimeZone timeZone = tz instanceof TimeZone ? (TimeZone) tz : TimeZone.getTimeZone(tz
            .toString());
        dateFormat.setTimeZone(timeZone);
      }
      return dateFormat.format(date);
    }

  },

  /**
   * <p>
   * Usage:
   * </p>
   *
   * <pre>
   *    {{numberFormat number ["format"] [locale=default]}}
   * </pre>
   *
   * Format parameters is one of:
   * <ul>
   * <li>"integer": the integer number format</li>
   * <li>"percent": the percent number format</li>
   * <li>"currency": the decimal number format</li>
   * <li>"pattern": a decimal pattern.</li>
   * </ul>
   * Otherwise, the default formatter will be used.
   *
   * <p>
   * More options:
   * </p>
   * <ul>
   * <li>groupingUsed: Set whether or not grouping will be used in this format.</li>
   * <li>maximumFractionDigits: Sets the maximum number of digits allowed in the fraction portion of
   * a number.</li>
   * <li>maximumIntegerDigits: Sets the maximum number of digits allowed in the integer portion of a
   * number</li>
   * <li>minimumFractionDigits: Sets the minimum number of digits allowed in the fraction portion of
   * a number</li>
   * <li>minimumIntegerDigits: Sets the minimum number of digits allowed in the integer portion of a
   * number.</li>
   * <li>parseIntegerOnly: Sets whether or not numbers should be parsed as integers only.</li>
   * <li>roundingMode: Sets the {@link java.math.RoundingMode} used in this NumberFormat.</li>
   * </ul>
   *
   * @see NumberFormat
   * @see DecimalFormat
   */
  numberFormat {
    @Override
    public Object apply(final Object context, final Options options) throws IOException {
      if (context instanceof Number) {
        return safeApply(context, options);
      }
      Object param = options.param(0, null);
      return param == null ? null : param.toString();
    }

    @Override
    protected CharSequence safeApply(final Object value, final Options options) {
      isTrue(value instanceof Number, "found '%s', expected 'number'", value);
      Number number = (Number) value;
      final NumberFormat numberFormat = build(options);

      Boolean groupingUsed = options.hash("groupingUsed");
      if (groupingUsed != null) {
        numberFormat.setGroupingUsed(groupingUsed);
      }

      Integer maximumFractionDigits = options.hash("maximumFractionDigits");
      if (maximumFractionDigits != null) {
        numberFormat.setMaximumFractionDigits(maximumFractionDigits);
      }

      Integer maximumIntegerDigits = options.hash("maximumIntegerDigits");
      if (maximumIntegerDigits != null) {
        numberFormat.setMaximumIntegerDigits(maximumIntegerDigits);
      }

      Integer minimumFractionDigits = options.hash("minimumFractionDigits");
      if (minimumFractionDigits != null) {
        numberFormat.setMinimumFractionDigits(minimumFractionDigits);
      }

      Integer minimumIntegerDigits = options.hash("minimumIntegerDigits");
      if (minimumIntegerDigits != null) {
        numberFormat.setMinimumIntegerDigits(minimumIntegerDigits);
      }

      Boolean parseIntegerOnly = options.hash("parseIntegerOnly");
      if (parseIntegerOnly != null) {
        numberFormat.setParseIntegerOnly(parseIntegerOnly);
      }

      String roundingMode = options.hash("roundingMode");
      if (roundingMode != null) {
        numberFormat.setRoundingMode(RoundingMode.valueOf(roundingMode.toUpperCase().trim()));
      }

      return numberFormat.format(number);
    }

    /**
     * Build a number format from options.
     *
     * @param options The helper options.
     * @return The number format to use.
     */
    private NumberFormat build(final Options options) {
      if (options.params.length == 0) {
        return NumberStyle.DEFAULT.numberFormat(Locale.getDefault());
      }
      isTrue(options.params[0] instanceof String, "found '%s', expected 'string'",
          options.params[0]);
      String format = options.param(0);
      String localeStr = options.param(1, Locale.getDefault().toString());
      Locale locale = LocaleUtils.toLocale(localeStr);
      try {
        NumberStyle style = NumberStyle.valueOf(format.toUpperCase().trim());
        return style.numberFormat(locale);
      } catch (ArrayIndexOutOfBoundsException ex) {
        return NumberStyle.DEFAULT.numberFormat(locale);
      } catch (IllegalArgumentException ex) {
        return new DecimalFormat(format, new DecimalFormatSymbols(locale));
      }
    }

  },

  /**
   * <p>
   * Usage:
   * </p>
   *
   * <pre>
   *    {{now ["format"] [tz=timeZone|timeZoneId]}}
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
  now {
    @Override
    protected CharSequence safeApply(final Object value, final Options options) {
      return StringHelpers.dateFormat.safeApply(new Date(), options);
    }

  };

  @Override
  public Object apply(final Object context, final Options options) throws IOException {
    if (options.isFalsy(context)) {
      Object param = options.param(0, null);
      return param == null ? null : param.toString();
    }
    return safeApply(context, options);
  }

  /**
   * Apply the helper to the context.
   *
   * @param context The context object (param=0).
   * @param options The options object.
   * @return A string result.
   */
  protected abstract CharSequence safeApply(final Object context, final Options options);

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

/**
 * Number format styles.
 *
 * @author edgar.espina
 * @since 1.0.1
 */
enum NumberStyle {

  /**
   * The default number format.
   */
  DEFAULT {
    @Override
    public NumberFormat numberFormat(final Locale locale) {
      return NumberFormat.getInstance(locale);
    }
  },

  /**
   * The integer number format.
   */
  INTEGER {
    @Override
    public NumberFormat numberFormat(final Locale locale) {
      return NumberFormat.getIntegerInstance(locale);
    }
  },

  /**
   * The currency number format.
   */
  CURRENCY {
    @Override
    public NumberFormat numberFormat(final Locale locale) {
      return NumberFormat.getCurrencyInstance(locale);
    }
  },

  /**
   * The percent number format.
   */
  PERCENT {
    @Override
    public NumberFormat numberFormat(final Locale locale) {
      return NumberFormat.getPercentInstance(locale);
    }
  };

  /**
   * Build a new number format.
   *
   * @param locale The locale to use.
   * @return A new number format.
   */
  public abstract NumberFormat numberFormat(Locale locale);
}
