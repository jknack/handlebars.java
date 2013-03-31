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
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.github.jknack.handlebars.cache.NullTemplateCache;
import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.helper.BlockHelper;
import com.github.jknack.handlebars.helper.EachHelper;
import com.github.jknack.handlebars.helper.EmbeddedHelper;
import com.github.jknack.handlebars.helper.I18nHelper;
import com.github.jknack.handlebars.helper.IfHelper;
import com.github.jknack.handlebars.helper.IncludeHelper;
import com.github.jknack.handlebars.helper.MethodHelper;
import com.github.jknack.handlebars.helper.PartialHelper;
import com.github.jknack.handlebars.helper.PrecompileHelper;
import com.github.jknack.handlebars.helper.UnlessHelper;
import com.github.jknack.handlebars.helper.WithHelper;
import com.github.jknack.handlebars.internal.HbsParserFactory;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * <p>
 * Handlebars provides the power necessary to let you build semantic templates effectively with no
 * frustration.
 * </p>
 * <h2>
 * Getting Started:</h2>
 *
 * <pre>
 * Handlebars handlebars = new Handlebars();
 * Template template = handlebars.compile("Hello {{this}}!");
 * System.out.println(template.apply("Handlebars.java"));
 * </pre>
 *
 * <h2>Loading templates</h2> Templates are loaded using the ```TemplateLoader``` class.
 * Handlebars.java provides three implementations of a ```TemplateLodaer```:
 * <ul>
 * <li>ClassPathTemplateLoader (default)</li>
 * <li>FileTemplateLoader</li>
 * <li>SpringTemplateLoader (available at the handlebars-springmvc module)</li>
 * </ul>
 *
 * <p>
 * This example load <code>mytemplate.hbs</code> from the root of the classpath:
 * </p>
 *
 * <pre>
 * Handlebars handlebars = new Handlebars();
 *
 * Template template = handlebars.compile(URI.create("mytemplate"));
 *
 * System.out.println(template.apply("Handlebars.java"));
 * </pre>
 *
 * <p>
 * You can specify a different ```TemplateLoader``` by:
 * </p>
 *
 * <pre>
 * TemplateLoader loader = ...;
 * Handlebars handlebars = new Handlebars(loader);
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
      result = prime * result + (content == null ? 0
          : content.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof SafeString) {
        SafeString that = (SafeString) obj;
        return content.equals(that.content);
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
      if (value instanceof Iterable) {
        return !((Iterable) value).iterator().hasNext();
      }
      if (value instanceof Boolean) {
        return !((Boolean) value).booleanValue();
      }
      if (value.getClass().isArray()) {
        return Array.getLength(value) == 0;
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
   * The missing helper's name.
   */
  public static final String HELPER_MISSING = "helperMissing";

  /**
   * The default start delimiter.
   */
  public static final String DELIM_START = "{{";

  /**
   * The default end delimiter.
   */
  public static final String DELIM_END = "}}";

  /**
   * The logging system.
   */
  private static final Logger logger = getLogger(Handlebars.class);

  /**
   * The template loader. Required.
   */
  private TemplateLoader loader;

  /**
   * The template cache. Required.
   */
  private TemplateCache cache;

  /**
   * If true, missing helper parameters will be resolve to their names.
   */
  private boolean stringParams;

  /**
   * If true, unnecessary whitespace and new lines will be removed.
   */
  private boolean prettyWhitespaces;

  /**
   * The helper registry.
   */
  private final Map<String, Helper<Object>> helpers =
      new HashMap<String, Helper<Object>>();

  /**
   * If true, templates will be able to call him self directly or indirectly. Use with caution.
   * Default is: false.
   */
  private boolean allowInfiniteLoops;

  /**
   * The missing value resolver strategy.
   */
  private MissingValueResolver missingValueResolver = MissingValueResolver.NULL;

  /**
   * The parser factory. Required.
   */
  private ParserFactory parserFactory = new HbsParserFactory();

  {
    // make sure default helpers are registered
    registerBuiltinsHelpers(this);
  }

  /**
   * Creates a new {@link Handlebars}.
   *
   * @param loader The template loader. Required.
   * @param cache The template cache. Required.
   */
  public Handlebars(final TemplateLoader loader, final TemplateCache cache) {
    with(loader);
    with(cache);
  }

  /**
   * Creates a new {@link Handlebars} with no cache.
   *
   * @param loader The template loader. Required.
   */
  public Handlebars(final TemplateLoader loader) {
    this(loader, NullTemplateCache.INSTANCE);
  }

  /**
   * Creates a new {@link Handlebars} with a {@link ClassPathTemplateLoader} and no
   * cache.
   */
  public Handlebars() {
    this(new ClassPathTemplateLoader(), NullTemplateCache.INSTANCE);
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
    return compile(loader.sourceAt(uri), startDelimiter, endDelimiter);
  }

  /**
   * Compile a handlebars template.
   *
   * @param input The handlebars input. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final String input) throws IOException {
    return compile(input, DELIM_START, DELIM_END);
  }

  /**
   * Compile a handlebars template.
   *
   * @param input The input text. Required.
   * @param startDelimiter The start delimiter. Required.
   * @param endDelimiter The end delimiter. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final String input, final String startDelimiter,
      final String endDelimiter) throws IOException {
    notNull(input, "The input is required.");
    String filename = "inline@" + Integer.toHexString(Math.abs(input.hashCode()));
    return compile(new StringTemplateSource(loader.resolve(URI.create(filename)), input),
        startDelimiter, endDelimiter);
  }

  /**
   * Compile a handlebars template.
   *
   * @param source The template source. Required.
   * @return A handlebars template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final TemplateSource source) throws IOException {
    return compile(source, DELIM_START, DELIM_END);
  }

  /**
   * Compile a handlebars template.
   *
   * @param source The template source. Required.
   * @param startDelimiter The start delimiter. Required.
   * @param endDelimiter The end delimiter. Required.
   * @return A handlebars template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final TemplateSource source, final String startDelimiter,
      final String endDelimiter) throws IOException {
    notNull(source, "The template source is required.");
    notEmpty(startDelimiter, "The start delimiter is required.");
    notEmpty(endDelimiter, "The end delimiter is required.");
    Parser parser = parserFactory.create(this, startDelimiter, endDelimiter);
    Template template = cache.get(source, parser);
    return template;
  }

  /**
   * Find a helper by it's name.
   *
   * @param <C> The helper runtime type.
   * @param name The helper's name. Required.
   * @return A helper or null if it's not found.
   */
  @SuppressWarnings("unchecked")
  public <C> Helper<C> helper(final String name) {
    notEmpty(name, "A helper's name is required.");
    return (Helper<C>) helpers.get(name);
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
  public <H> Handlebars registerHelper(final String name, final Helper<H> helper) {
    notEmpty(name, "A helper's name is required.");
    notNull(helper, "A helper is required.");
    Helper<Object> oldHelper = helpers.put(name, (Helper<Object>) helper);
    if (oldHelper != null) {
      warn("Helper '%s': %s has been replaced by %s", name, oldHelper, helper);
    }
    return this;
  }

  /**
   * <p>
   * Register all the helper methods for the given helper source.
   * </p>
   * <p>
   * A helper method looks like:
   * </p>
   *
   * <pre>
   * public static? CharSequence methodName(context?, parameter*, options?) {
   * }
   * </pre>
   *
   * Where:
   * <ul>
   * <li>A method can/can't be static</li>
   * <li>The method's name became the helper's name</li>
   * <li>Context, parameters and options are all optional</li>
   * <li>If context and options are present they must be the first and last arguments of
   * the method</li>
   * </ul>
   *
   * Instance and static methods will be registered as helpers.
   *
   * @param helperSource The helper source. Required.
   * @return This handlebars object.
   */
  public Handlebars registerHelpers(final Object helperSource) {
    notNull(helperSource, "The helper source is required.");
    registerDynamicHelper(helperSource, helperSource.getClass());
    return this;
  }

  /**
   * <p>
   * Register all the helper methods for the given helper source.
   * </p>
   * <p>
   * A helper method looks like:
   * </p>
   *
   * <pre>
   * public static? CharSequence methodName(context?, parameter*, options?) {
   * }
   * </pre>
   *
   * Where:
   * <ul>
   * <li>A method can/can't be static</li>
   * <li>The method's name became the helper's name</li>
   * <li>Context, parameters and options are all optional</li>
   * <li>If context and options are present they must be the first and last arguments of
   * the method</li>
   * </ul>
   *
   * Only static methods will be registered as helpers.
   *
   * @param helperSource The helper source. Required.
   * @return This handlebars object.
   */
  public Handlebars registerHelpers(final Class<?> helperSource) {
    notNull(helperSource, "The helper source is required.");
    registerDynamicHelper(null, helperSource);
    return this;
  }

  /**
   * <p>
   * Register all the helper methods for the given helper source.
   * </p>
   *
   * @param source The helper source.
   * @param clazz The helper source class.
   */
  private void registerDynamicHelper(final Object source, final Class<?> clazz) {
    int size = helpers.size();
    if (clazz != Object.class) {
      Set<String> overloaded = new HashSet<String>();
      // Keep backing up the inheritance hierarchy.
      Method[] methods = clazz.getDeclaredMethods();
      for (Method method : methods) {
        boolean isPublic = Modifier.isPublic(method.getModifiers());
        String helperName = method.getName();
        if (isPublic && CharSequence.class.isAssignableFrom(method.getReturnType())) {
          boolean isStatic = Modifier.isStatic(method.getModifiers());
          if (source != null || isStatic) {
            isTrue(overloaded.add(helperName), "name conflict found: " + helperName);
            registerHelper(helperName, new MethodHelper(method, source));
          }
        }
      }
    }
    isTrue(size != helpers.size(), "No helper method was found in: " + clazz.getName());
  }

  /**
   * The resource locator.
   *
   * @return The resource locator.
   */
  public TemplateLoader getLoader() {
    return loader;
  }

  /**
   * The template cache.
   *
   * @return The template cache.
   */
  public TemplateCache getCache() {
    return cache;
  }

  /**
   * The missing value resolver.
   *
   * @return The missing value resolver.
   */
  public MissingValueResolver getMissingValueResolver() {
    return missingValueResolver;
  }

  /**
   * If true, missing helper parameters will be resolve to their names.
   *
   * @return If true, missing helper parameters will be resolve to their names.
   */
  public boolean allowStringParams() {
    return stringParams;
  }

  /**
   * If true, unnecessary spaces and new lines will be removed from output. Default is: false.
   *
   * @return If true, unnecessary spaces and new lines will be removed from output. Default is:
   *         false.
   */
  public boolean prettyWhitespaces() {
    return prettyWhitespaces;
  }

  /**
   * If true, unnecessary spaces and new lines will be removed from output. Default is: false.
   *
   * @param prettyWhitespaces If true, unnecessary spaces and new lines will be removed from output.
   *        Default is: false.
   */
  public void setPrettyWhitespaces(final boolean prettyWhitespaces) {
    this.prettyWhitespaces = prettyWhitespaces;
  }

  /**
   * If true, missing helper parameters will be resolve to their names.
   *
   * @param stringParams If true, missing helper parameters will be resolve to
   *        their names.
   */
  public void setStringParams(final boolean stringParams) {
    this.stringParams = stringParams;
  }

  /**
   * If true, templates will be able to call him self directly or indirectly. Use with caution.
   * Default is: false.
   *
   * @return If true, templates will be able to call him self directly or indirectly. Use with
   *         caution. Default is: false.
   */
  public boolean allowInfiniteLoops() {
    return allowInfiniteLoops;
  }

  /**
   * If true, templates will be able to call him self directly or indirectly. Use with caution.
   * Default is: false.
   *
   * @param allowInfiniteLoops If true, templates will be able to call him self directly or
   *        indirectly.
   */
  public void setAllowInfiniteLoops(final boolean allowInfiniteLoops) {
    this.allowInfiniteLoops = allowInfiniteLoops;
  }

  /**
   * Set a new {@link TemplateLoader}. Default is: {@link ClassPathTemplateLoader}.
   *
   * @param loader The template loader. Required.
   * @return This handlebars object.
   */
  public Handlebars with(final TemplateLoader loader) {
    this.loader = notNull(loader, "The template loader is required.");
    return this;
  }

  /**
   * Set a new {@link ParserFactory}.
   *
   * @param parserFactory A parser factory. Required.
   * @return This handlebars object.
   */
  public Handlebars with(final ParserFactory parserFactory) {
    this.parserFactory = notNull(parserFactory, "A parserFactory is required.");
    return this;
  }

  /**
   * Set a new {@link TemplateCache}.
   *
   * @param cache The template cache. Required.
   * @return This handlebars object.
   */
  public Handlebars with(final TemplateCache cache) {
    this.cache = notNull(cache, "The template loader is required.");
    return this;
  }

  /**
   * Set a new {@link MissingValueResolver}.
   *
   * @param missingValueResolver The missing value resolver. Required.
   * @return This handlebars object.
   */
  public Handlebars with(final MissingValueResolver missingValueResolver) {
    this.missingValueResolver = notNull(missingValueResolver,
        "The missing value resolver is required.");
    return this;
  }

  /**
   * Return a parser factory.
   *
   * @return A parsert factory.
   */
  public ParserFactory getParserFactory() {
    return parserFactory;
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
    handlebars.registerHelper(IncludeHelper.NAME, IncludeHelper.INSTANCE);
    handlebars.registerHelper(PrecompileHelper.NAME,
        PrecompileHelper.INSTANCE);
    I18nHelper.registerHelpers(handlebars);
  }

}
