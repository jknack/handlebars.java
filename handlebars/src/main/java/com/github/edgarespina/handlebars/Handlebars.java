/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.edgarespina.handlebars;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.github.edgarespina.handlebars.helper.BlockHelper;
import com.github.edgarespina.handlebars.helper.EachHelper;
import com.github.edgarespina.handlebars.helper.EachPseudoVarHelper;
import com.github.edgarespina.handlebars.helper.EmbeddedHelper;
import com.github.edgarespina.handlebars.helper.IfHelper;
import com.github.edgarespina.handlebars.helper.PartialHelper;
import com.github.edgarespina.handlebars.helper.UnlessHelper;
import com.github.edgarespina.handlebars.helper.WithHelper;
import com.github.edgarespina.handlebars.internal.Parser;
import com.github.edgarespina.handlebars.io.ClassTemplateLoader;

/**
 * <p>
 * Handlebars provides the power necessary to let you build semantic templates
 * effectively with no frustration.
 * </p>
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 * Handlebars handlebars = new Handlebars();
 *
 * Template template = handlebars.compile(&quot;Hello {{name}}!&quot;);
 *
 * Person person = new Person(&quot;John&quot;, &quot;Doe&quot;);
 *
 * String output = template.apply(person);
 *
 * assertEquals(&quot;Hello John!&quot;, output);
 * </pre>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class Handlebars {

  /**
   * A {@link SafeString} tell {@link Handlebars} that the content should not be
   * escaped as HTML.
   *
   * @author edgar.espina
   * @since 0.1.0
   */
  public static class SafeString implements CharSequence {

    /**
     * The content.
     */
    private CharSequence content;

    /**
     * Creates a new {@link SafeString}.
     *
     * @param content The string content.
     */
    public SafeString(final CharSequence content) {
      this.content = content;
    }

    @Override
    public int length() {
      return content.length();
    }

    @Override
    public char charAt(final int index) {
      return content.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
      return content.subSequence(start, end);
    }

    @Override
    public String toString() {
      return content.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((content == null) ? 0
          : content.toString().hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof SafeString) {
        SafeString that = (SafeString) obj;
        return this.content.toString().equals(that.content.toString());
      }
      return false;
    }
  }

  /**
   * Utilities function like: {@link Utils#escapeExpression(CharSequence)} and
   * {@link Utils#isEmpty(Object)}.
   *
   * @author edgar.espina
   * @since 0.1.0
   */
  public static class Utils {

    /**
     * Evaluate the given object and return true is the object is considered
     * empty. Nulls, empty list or array and false values are considered empty.
     *
     * @param value The object value.
     * @return Return true is the object is considered empty. Nulls, empty list
     *         or array and false values are considered empty.
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(final Object value) {
      if (value == null) {
        return true;
      }
      if (value instanceof CharSequence) {
        return ((CharSequence) value).length() == 0;
      }
      if (value instanceof Collection) {
        return ((Collection) value).size() == 0;
      }
      if (value.getClass().isArray()) {
        return Array.getLength(value) == 0;
      }
      if (value instanceof Iterable) {
        return !((Iterable) value).iterator().hasNext();
      }
      if (value instanceof Boolean) {
        return !((Boolean) value).booleanValue();
      }
      return false;
    }

    /**
     * <p>
     * Escapes the characters in a {@code String} using HTML entities.
     * </p>
     * <p>
     * For example:
     * </p>
     * <p>
     * <code>"bread" & "butter"</code>
     * </p>
     * becomes:
     *
     * <pre>
     *  &amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;
     * </pre>
     *
     * @param input the {@code String} to escape, may be null.
     * @return The escaped version of the input or the same input if it's a
     *         SafeString.
     */
    public static String escapeExpression(final CharSequence input) {
      if (input == null || input.length() == 0) {
        return "";
      }
      // Don't escape SafeStrings, since they're already safe
      if (input instanceof SafeString) {
        return input.toString();
      }
      StringBuilder html = new StringBuilder(input.length());
      for (int i = 0; i < input.length(); i++) {
        char ch = input.charAt(i);
        switch (ch) {
          case '<':
            html.append("&lt;");
            break;
          case '>':
            html.append("&gt;");
            break;
          case '"':
            html.append("&quot;");
            break;
          case '\'':
            html.append("&#x27;");
            break;
          case '`':
            html.append("&#x60;");
            break;
          case '&':
            html.append("&amp;");
            break;
          default:
            html.append(ch);
        }
      }
      return html.toString();
    }
  }

  /**
   * The default start delimiter.
   */
  private static final String DELIM_START = "{{";

  /**
   * The default end delimiter.
   */
  private static final String DELIM_END = "}}";

  /**
   * NO CACHE.
   */
  private static final TemplateCache NO_CACHE = new TemplateCache() {
    @Override
    public void put(final Object key, final Template template) {
    }

    @Override
    public Template get(final Object key) {
      return null;
    }

    @Override
    public void evict(final Object key) {
    }

    @Override
    public void clear() {
    }
  };
  /**
   * The logging system.
   */
  private static final Logger logger = getLogger(Handlebars.class);

  /**
   * The template loader. Required.
   */
  private final TemplateLoader loader;

  /**
   * The template cache. Required.
   */
  private final TemplateCache cache;

  /**
   * The helper registry.
   */
  private final Map<String, Helper<Object>> helpers =
      new HashMap<String, Helper<Object>>();

  static {
    /**
     * Initialize the parser and speed up for later.
     */
    Parser.initialize();
  }

  /**
   * Creates a new {@link Handlebars}.
   *
   * @param loader The template loader. Required.
   * @param cache The template cache. Required.
   */
  public Handlebars(final TemplateLoader loader, final TemplateCache cache) {
    this.loader =
        checkNotNull(loader, "The template loader is required.");
    this.cache =
        checkNotNull(cache, "The template cache is required.");

    registerBuiltinsHelpers(this);
  }

  /**
   * Creates a new {@link Handlebars} with no cache.
   *
   * @param loader The template loader. Required.
   */
  public Handlebars(final TemplateLoader loader) {
    this(loader, NO_CACHE);
  }

  /**
   * Creates a new {@link Handlebars} with a {@link ClassTemplateLoader} and no
   * cache.
   */
  public Handlebars() {
    this(new ClassTemplateLoader(), NO_CACHE);
  }

  /**
   * Compile the resource located at the given uri.
   *
   * @param uri The resource's location. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final URI uri) throws IOException {
    return compile(uri, DELIM_START, DELIM_END);
  }

  /**
   * Compile the resource located at the given uri.
   *
   * @param uri The resource's location. Required.
   * @param startDelimiter The start delimiter. Required.
   * @param endDelimiter The end delimiter. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final URI uri, final String startDelimiter,
      final String endDelimiter) throws IOException {
    checkNotNull(uri, "The uri is required.");
    checkArgument(uri.toString().length() > 0, "The uri is required.");
    checkDelimiters(startDelimiter, endDelimiter);
    String key = uri + "_" + startDelimiter + endDelimiter;
    debug("Looking for: %s", key);
    Template template = cache.get(key);
    if (template == null) {
      debug("Key not found: %s", key);
      Reader reader = loader.load(uri);
      template =
          Parser.create(this, uri.toString(), startDelimiter, endDelimiter)
              .parse(reader);
      cache.put(key, template);
      debug("Key saved: %s", key);
    }
    return template;
  }

  /**
   * Compile the given input.
   *
   * @param input The resource's input. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final String input) throws IOException {
    return compile(input, DELIM_START, DELIM_END);
  }

  /**
   * Compile the given input.
   *
   * @param input The input text. Required.
   * @param startDelimiter The start delimiter. Required.
   * @param endDelimiter The end delimiter. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final String input, final String startDelimiter,
      final String endDelimiter) throws IOException {
    checkNotNull(input, "The input text is required.");
    checkArgument(input.length() > 0, "The input text is required.");
    String key = input.hashCode() + "_" + startDelimiter + endDelimiter;
    debug("Looking for: %s", key);
    Template template = cache.get(key);
    if (template == null) {
      debug("Key not found: %s", key);
      template =
          Parser.create(this, "embedded", startDelimiter, endDelimiter).parse(
              input);
      cache.put(key, template);
      debug("Key saved: %s", key);
    }
    return template;
  }

  /**
   * Find a helper by it's name.
   *
   * @param <H> The helper runtime type.
   * @param name The helper's name. Required.
   * @return A helper or null if it's not found.
   */
  @SuppressWarnings("unchecked")
  public <H> Helper<H> helper(final String name) {
    checkNotNull(name, "A helper's name is required.");
    return (Helper<H>) helpers.get(name);
  }

  /**
   * Register a helper in the helper registry.
   *
   * @param <H> The helper runtime type.
   * @param name The helper's name. Required.
   * @param helper The helper object. Required.
   * @return This handlebars.
   */
  @SuppressWarnings("unchecked")
  public <H> Handlebars registerHelper(final String name,
      final Helper<H> helper) {
    checkNotNull(name, "A helper's name is required.");
    checkNotNull(helper, "A helper is required.");
    helpers.put(name, (Helper<Object>) helper);
    return this;
  }

  /**
   * Indicates if Handlebars should publish pseudo variables.
   *
   * @param exposePseudoVariables True if Handlebars should add pseudo variables
   *        like: <code>@index, @first, @last, @key, @value</code>. Default is:
   *        false.
   * @return This handlebars.
   */
  public Handlebars setExposePseudoVariables(
      final boolean exposePseudoVariables) {
    if (exposePseudoVariables) {
      registerHelper(EachPseudoVarHelper.NAME, EachPseudoVarHelper.INSTANCE);
    }
    return this;
  }

  /**
   * The resource locator.
   *
   * @return The resource locator.
   */
  public TemplateLoader getTemplateLoader() {
    return loader;
  }

  /**
   * Log the given message and format the message within the args.
   *
   * @param message The log's message.
   * @param args The optional args.
   * @see String#format(String, Object...)
   */
  public static void log(final String message, final Object... args) {
    logger.info(String.format(message, args));
  }

  /**
   * Log the given message and format the message within the args.
   *
   * @param message The log's message.
   * @see String#format(String, Object...)
   */
  public static void log(final String message) {
    logger.info(message);
  }

  /**
   * Log the given message as warn and format the message within the args.
   *
   * @param message The log's message.
   * @param args The optional args.
   * @see String#format(String, Object...)
   */
  public static void warn(final String message, final Object... args) {
    if (logger.isWarnEnabled()) {
      logger.warn(String.format(message, args));
    }
  }

  /**
   * Log the given message as warn and format the message within the args.
   *
   * @param message The log's message.
   * @see String#format(String, Object...)
   */
  public static void warn(final String message) {
    logger.warn(message);
  }

  /**
   * Log the given message as debug and format the message within the args.
   *
   * @param message The log's message.
   * @param args The optional args.
   * @see String#format(String, Object...)
   */
  public static void debug(final String message, final Object... args) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format(message, args));
    }
  }

  /**
   * Log the given message as debug and format the message within the args.
   *
   * @param message The log's message.
   * @see String#format(String, Object...)
   */
  public static void debug(final String message) {
    logger.debug(message);
  }

  /**
   * Log the given message as error and format the message within the args.
   *
   * @param message The log's message.
   * @param args The optional args.
   * @see String#format(String, Object...)
   */

  public static void error(final String message, final Object... args) {
    logger.error(String.format(message, args));
  }

  /**
   * Log the given message as error and format the message within the args.
   *
   * @param message The log's message.
   * @see String#format(String, Object...)
   */
  public static void error(final String message) {
    logger.error(message);
  }

  /**
   * Check if the given delimiters aren't empty.
   *
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   */
  private static void checkDelimiters(final String startDelimiter,
      final String endDelimiter) {
    checkNotNull(startDelimiter, "The start delimiter is required.");
    checkArgument(startDelimiter.length() > 0,
        "The start delimiter is required.");
    checkNotNull(endDelimiter, "The end delimiter is required.");
    checkArgument(endDelimiter.length() > 0, "The end delimiter is required.");
  }

  /**
   * Register built-in helpers.
   *
   * @param handlebars The handlebars instance.
   */
  private static void registerBuiltinsHelpers(final Handlebars handlebars) {
    handlebars.registerHelper(WithHelper.NAME, WithHelper.INSTANCE);
    handlebars.registerHelper(IfHelper.NAME, IfHelper.INSTANCE);
    handlebars.registerHelper(UnlessHelper.NAME, UnlessHelper.INSTANCE);
    handlebars.registerHelper(EachHelper.NAME, EachHelper.INSTANCE);
    handlebars.registerHelper(EmbeddedHelper.NAME, EmbeddedHelper.INSTANCE);
    handlebars.registerHelper(BlockHelper.NAME, BlockHelper.INSTANCE);
    handlebars.registerHelper(PartialHelper.NAME, PartialHelper.INSTANCE);
  }
}
