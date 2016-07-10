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
package com.github.jknack.handlebars;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;

import com.github.jknack.handlebars.cache.NullTemplateCache;
import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.helper.DefaultHelperRegistry;
import com.github.jknack.handlebars.internal.FormatterChain;
import com.github.jknack.handlebars.internal.HbsParserFactory;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
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
 * Template template = handlebars.compileInline("Hello {{this}}!");
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
 * Template template = handlebars.compileInline(URI.create("mytemplate"));
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
public class Handlebars implements HelperRegistry {

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
    public final CharSequence content;

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
      if (value instanceof Number) {
        return ((Number) value).doubleValue() == 0;
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
     * <code>"bread" {@literal &} "butter"</code>
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
    public static CharSequence escapeExpression(final CharSequence input) {
      return EscapingStrategy.DEF.escape(input);
    }
  }

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
  private TemplateCache cache = NullTemplateCache.INSTANCE;

  /**
   * If true, missing helper parameters will be resolve to their names.
   */
  private boolean stringParams;

  /**
   * If true, unnecessary whitespace and new lines will be removed.
   */
  private boolean prettyPrint;

  /**
   * The helper registry.
   */
  private HelperRegistry registry = new DefaultHelperRegistry();

  /**
   * If true, templates will be able to call him self directly or indirectly. Use with caution.
   * Default is: false.
   */
  private boolean infiniteLoops;

  /**
   * If true, templates will be deleted once applied. Useful, in some advanced template inheritance
   * use cases. Default is: false.
   * At any time you can override the default setup with:
   *
   * <pre>
   * {{#block "footer" delete-after-merge=true}}
   * </pre>
   */
  private boolean deletePartialAfterMerge;

  /**
   * The escaping strategy.
   */
  private EscapingStrategy escapingStrategy = EscapingStrategy.HTML_ENTITY;

  /**
   * The parser factory. Required.
   */
  private ParserFactory parserFactory = new HbsParserFactory();

  /**
   * The start delimiter.
   */
  private String startDelimiter = DELIM_START;

  /**
   * The end delimiter.
   */
  private String endDelimiter = DELIM_END;

  /** Location of the handlebars.js file. */
  private String handlebarsJsFile = "/handlebars-v4.0.4.js";

  /** List of formatters. */
  private List<Formatter> formatters = new ArrayList<Formatter>();

  /** Default formatter. */
  private Formatter.Chain formatter = Formatter.NOOP;

  /** True, if we want to extend lookup to parent scope. */
  private boolean parentScopeResolution = true;

  /**
   * Creates a new {@link Handlebars} with no cache.
   *
   * @param loader The template loader. Required.
   */
  public Handlebars(final TemplateLoader loader) {
    with(loader);
  }

  /**
   * Creates a new {@link Handlebars} with a {@link ClassPathTemplateLoader} and no
   * cache.
   */
  public Handlebars() {
    this(new ClassPathTemplateLoader());
  }

  /**
   * Compile the resource located at the given uri.
   *
   * @param location The resource's location. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final String location) throws IOException {
    return compile(location, startDelimiter, endDelimiter);
  }

  /**
   * Compile the resource located at the given uri.
   *
   * @param location The resource's location. Required.
   * @param startDelimiter The start delimiter. Required.
   * @param endDelimiter The end delimiter. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final String location, final String startDelimiter,
      final String endDelimiter) throws IOException {
    return compile(loader.sourceAt(location), startDelimiter, endDelimiter);
  }

  /**
   * Compile a handlebars template.
   *
   * @param input The handlebars input. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compileInline(final String input) throws IOException {
    return compileInline(input, startDelimiter, endDelimiter);
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
  public Template compileInline(final String input, final String startDelimiter,
      final String endDelimiter) throws IOException {
    notNull(input, "The input is required.");
    String filename = "inline@" + Integer.toHexString(Math.abs(input.hashCode()));
    return compile(new StringTemplateSource(filename, input),
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
    return compile(source, startDelimiter, endDelimiter);
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
   * Find a helper by name.
   *
   * @param <C> The helper runtime type.
   * @param name The helper's name. Required.
   * @return A helper or null if it's not found.
   */
  @Override
  public <C> Helper<C> helper(final String name) {
    return registry.helper(name);
  }

  /**
   * Register a helper in the helper registry.
   *
   * @param <H> The helper runtime type.
   * @param name The helper's name. Required.
   * @param helper The helper object. Required.
   * @return This handlebars.
   */
  @Override
  public <H> Handlebars registerHelper(final String name, final Helper<H> helper) {
    registry.registerHelper(name, helper);
    return this;
  }

  /**
   * Register a missing helper in the helper registry.
   *
   * @param <H> The helper runtime type.
   * @param helper The helper object. Required.
   * @return This handlebars.
   */
  @Override
  public <H> Handlebars registerHelperMissing(final Helper<H> helper) {
    return registerHelper(HELPER_MISSING, helper);
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
   * <li>If context and options are present they must be the first and last method arguments.</li>
   * </ul>
   *
   * Instance and static methods will be registered as helpers.
   *
   * @param helperSource The helper source. Required.
   * @return This handlebars object.
   */
  @Override
  public Handlebars registerHelpers(final Object helperSource) {
    registry.registerHelpers(helperSource);
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
   * <li>If context and options are present they must be the first and last method arguments.</li>
   * </ul>
   *
   * Only static methods will be registered as helpers.
   * <p>
   * Enums are supported too
   * </p>
   *
   * @param helperSource The helper source. Enums are supported. Required.
   * @return This handlebars object.
   */
  @Override
  public Handlebars registerHelpers(final Class<?> helperSource) {
    registry.registerHelpers(helperSource);
    return this;
  }

  /**
   * <p>
   * Register helpers from a JavaScript source.
   * </p>
   * <p>
   * A JavaScript source file looks like:
   * </p>
   *
   * <pre>
   *  Handlebars.registerHelper('hey', function (context) {
   *    return 'Hi ' + context.name;
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, options) {
   *    return 'Hi ' + context.name + options.hash['x'];
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, p1, p2, options) {
   *    return 'Hi ' + context.name + p1 + p2 + options.hash['x'];
   *  });
   *  ...
   * </pre>
   *
   * To keep your helpers reusable between server and client avoid DOM manipulation.
   *
   * @param location A classpath location. Required.
   * @return This handlebars object.
   * @throws Exception If the JavaScript helpers can't be registered.
   */
  @Override
  public Handlebars registerHelpers(final URI location) throws Exception {
    registry.registerHelpers(location);
    return this;
  }

  /**
   * <p>
   * Register helpers from a JavaScript source.
   * </p>
   * <p>
   * A JavaScript source file looks like:
   * </p>
   *
   * <pre>
   *  Handlebars.registerHelper('hey', function (context) {
   *    return 'Hi ' + context.name;
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, options) {
   *    return 'Hi ' + context.name + options.hash['x'];
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, p1, p2, options) {
   *    return 'Hi ' + context.name + p1 + p2 + options.hash['x'];
   *  });
   *  ...
   * </pre>
   *
   * To keep your helpers reusable between server and client avoid DOM manipulation.
   *
   * @param input A JavaScript file name. Required.
   * @return This handlebars object.
   * @throws Exception If the JavaScript helpers can't be registered.
   */
  @Override
  public Handlebars registerHelpers(final File input) throws Exception {
    registry.registerHelpers(input);
    return this;
  }

  /**
   * <p>
   * Register helpers from a JavaScript source.
   * </p>
   * <p>
   * A JavaScript source file looks like:
   * </p>
   *
   * <pre>
   *  Handlebars.registerHelper('hey', function (context) {
   *    return 'Hi ' + context.name;
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, options) {
   *    return 'Hi ' + context.name + options.hash['x'];
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, p1, p2, options) {
   *    return 'Hi ' + context.name + p1 + p2 + options.hash['x'];
   *  });
   *  ...
   * </pre>
   *
   * To keep your helpers reusable between server and client avoid DOM manipulation.
   *
   * @param filename The file name (just for debugging purpose). Required.
   * @param source The JavaScript source. Required.
   * @return This handlebars object.
   * @throws Exception If the JavaScript helpers can't be registered.
   */
  @Override
  public Handlebars registerHelpers(final String filename, final Reader source) throws Exception {
    registry.registerHelpers(filename, source);
    return this;
  }

  /**
   * <p>
   * Register helpers from a JavaScript source.
   * </p>
   * <p>
   * A JavaScript source file looks like:
   * </p>
   *
   * <pre>
   *  Handlebars.registerHelper('hey', function (context) {
   *    return 'Hi ' + context.name;
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, options) {
   *    return 'Hi ' + context.name + options.hash['x'];
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, p1, p2, options) {
   *    return 'Hi ' + context.name + p1 + p2 + options.hash['x'];
   *  });
   *  ...
   * </pre>
   *
   * To keep your helpers reusable between server and client avoid DOM manipulation.
   *
   * @param filename The file name (just for debugging purpose). Required.
   * @param source The JavaScript source. Required.
   * @return This handlebars object.
   * @throws Exception If the JavaScript helpers can't be registered.
   */
  @Override
  public Handlebars registerHelpers(final String filename, final InputStream source)
      throws Exception {
    registry.registerHelpers(filename, source);
    return this;
  }

  /**
   * <p>
   * Register helpers from a JavaScript source.
   * </p>
   * <p>
   * A JavaScript source file looks like:
   * </p>
   *
   * <pre>
   *  Handlebars.registerHelper('hey', function (context) {
   *    return 'Hi ' + context.name;
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, options) {
   *    return 'Hi ' + context.name + options.hash['x'];
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, p1, p2, options) {
   *    return 'Hi ' + context.name + p1 + p2 + options.hash['x'];
   *  });
   *  ...
   * </pre>
   *
   * To keep your helpers reusable between server and client avoid DOM manipulation.
   *
   * @param filename The file name (just for debugging purpose). Required.
   * @param source The JavaScript source. Required.
   * @return This handlebars object.
   * @throws Exception If the JavaScript helpers can't be registered.
   */
  @Override
  public Handlebars registerHelpers(final String filename, final String source) throws Exception {
    registry.registerHelpers(filename, source);
    return this;
  }

  @Override
  public Set<Entry<String, Helper<?>>> helpers() {
    return registry.helpers();
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
   * The escaping strategy.
   *
   * @return The escaping strategy.
   */
  public EscapingStrategy getEscapingStrategy() {
    return escapingStrategy;
  }

  /**
   * If true, missing helper parameters will be resolve to their names.
   *
   * @return If true, missing helper parameters will be resolve to their names.
   */
  public boolean stringParams() {
    return stringParams;
  }

  /**
   * If true, unnecessary spaces and new lines will be removed from output. Default is: false.
   *
   * @return If true, unnecessary spaces and new lines will be removed from output. Default is:
   *         false.
   */
  public boolean prettyPrint() {
    return prettyPrint;
  }

  /**
   * If true, unnecessary spaces and new lines will be removed from output. Default is: false.
   *
   * @param prettyPrint If true, unnecessary spaces and new lines will be removed from output.
   *        Default is: false.
   */
  public void setPrettyPrint(final boolean prettyPrint) {
    this.prettyPrint = prettyPrint;
  }

  /**
   * If true, unnecessary spaces and new lines will be removed from output. Default is: false.
   *
   * @param prettyPrint If true, unnecessary spaces and new lines will be removed from output.
   *        Default is: false.
   * @return This handlebars object.
   */
  public Handlebars prettyPrint(final boolean prettyPrint) {
    setPrettyPrint(prettyPrint);
    return this;
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
   * If true, missing helper parameters will be resolve to their names.
   *
   * @param stringParams If true, missing helper parameters will be resolve to
   *        their names.
   * @return The handlebars object.
   */
  public Handlebars stringParams(final boolean stringParams) {
    setStringParams(stringParams);
    return this;
  }

  /**
   * If true, templates will be able to call him self directly or indirectly. Use with caution.
   * Default is: false.
   *
   * @return If true, templates will be able to call him self directly or indirectly. Use with
   *         caution. Default is: false.
   */
  public boolean infiniteLoops() {
    return infiniteLoops;
  }

  /**
   * If true, templates will be able to call him self directly or indirectly. Use with caution.
   * Default is: false.
   *
   * @param infiniteLoops If true, templates will be able to call him self directly or
   *        indirectly.
   */
  public void setInfiniteLoops(final boolean infiniteLoops) {
    this.infiniteLoops = infiniteLoops;
  }

  /**
   * If true, templates will be able to call him self directly or indirectly. Use with caution.
   * Default is: false.
   *
   * @param infiniteLoops If true, templates will be able to call him self directly or
   *        indirectly.
   * @return The handlebars object.
   */
  public Handlebars infiniteLoops(final boolean infiniteLoops) {
    setInfiniteLoops(infiniteLoops);
    return this;
  }

  /**
   * If true, templates will be deleted once applied. Useful, in some advanced template inheritance
   * use cases. Used by <code>{{#block}} helper</code>. Default is: false.
   * At any time you can override the default setup with:
   *
   * <pre>
   * {{#block "footer" delete-after-merge=true}}
   * </pre>
   *
   * @return True for clearing up templates once they got applied. Used by
   *         <code>{{#block}} helper</code>.
   */
  public boolean deletePartialAfterMerge() {
    return deletePartialAfterMerge;
  }

  /**
   * If true, templates will be deleted once applied. Useful, in some advanced template inheritance
   * use cases. Used by <code>{{#block}} helper</code>. Default is: false.
   * At any time you can override the default setup with:
   *
   * <pre>
   * {{#block "footer" delete-after-merge=true}}
   * </pre>
   *
   * @param deletePartialAfterMerge True for clearing up templates once they got applied. Used by
   *        <code>{{#block}} helper</code>.
   *
   * @return This handlebars object.
   */
  public Handlebars deletePartialAfterMerge(final boolean deletePartialAfterMerge) {
    setDeletePartialAfterMerge(deletePartialAfterMerge);
    return this;
  }

  /**
   * If true, templates will be deleted once applied. Useful, in some advanced template inheritance
   * use cases. Used by <code>{{#block}} helper</code>. Default is: false.
   * At any time you can override the default setup with:
   *
   * <pre>
   * {{#block "footer" delete-after-merge=true}}
   * </pre>
   *
   * @param deletePartialAfterMerge True for clearing up templates once they got applied. Used by
   *        <code>{{#block}} helper</code>.
   */
  public void setDeletePartialAfterMerge(final boolean deletePartialAfterMerge) {
    this.deletePartialAfterMerge = deletePartialAfterMerge;
  }

  /**
   * Set the end delimiter.
   *
   * @param endDelimiter The end delimiter. Required.
   */
  public void setEndDelimiter(final String endDelimiter) {
    this.endDelimiter = notEmpty(endDelimiter, "The endDelimiter is required.");
  }

  /**
   * Set the end delimiter.
   *
   * @param endDelimiter The end delimiter. Required.
   * @return This handlebars object.
   */
  public Handlebars endDelimiter(final String endDelimiter) {
    setEndDelimiter(endDelimiter);
    return this;
  }

  /**
   * Set the start delimiter.
   *
   * @param startDelimiter The start delimiter. Required.
   */
  public void setStartDelimiter(final String startDelimiter) {
    this.startDelimiter = notEmpty(startDelimiter, "The startDelimiter is required.");
  }

  /**
   * Set the start delimiter.
   *
   * @param startDelimiter The start delimiter. Required.
   * @return This handlebars object.
   */
  public Handlebars startDelimiter(final String startDelimiter) {
    setStartDelimiter(startDelimiter);
    return this;
  }

  /**
   * Set one or more {@link TemplateLoader}. In the case of two or more {@link TemplateLoader}, a
   * {@link CompositeTemplateLoader} will be created. Default is: {@link ClassPathTemplateLoader}.
   *
   * @param loader The template loader. Required.
   * @return This handlebars object.
   * @see CompositeTemplateLoader
   */
  public Handlebars with(final TemplateLoader... loader) {
    isTrue(loader.length > 0, "The template loader is required.");
    this.loader = loader.length == 1 ? loader[0] : new CompositeTemplateLoader(loader);
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
    this.cache = notNull(cache, "The template cache is required.");
    return this;
  }

  /**
   * Set the helper registry. This operation will override will remove any previously registered
   * helper.
   *
   * @param registry The helper registry. Required.
   * @return This handlebars object.
   */
  public Handlebars with(final HelperRegistry registry) {
    this.registry = notNull(registry, "The registry is required.");

    return this;
  }

  /**
   * Set a new {@link EscapingStrategy}.
   *
   * @param escapingStrategy The escaping strategy. Required.
   * @return This handlebars object.
   */
  public Handlebars with(final EscapingStrategy escapingStrategy) {
    this.escapingStrategy = notNull(escapingStrategy,
        "The escaping strategy is required.");
    return this;
  }

  /**
   * @return A formatter chain.
   */
  public Formatter.Chain getFormatter() {
    return formatter;
  }

  /**
   * Add a new variable formatter.
   *
   * <pre>
   *
   * Handlebars hbs = new Handlebars();
   *
   * hbs.with(new Formatter() {
   *   public Object format(Object value, Chain next) {
   *    if (value instanceof Date) {
   *      return ((Date) value).getTime();
   *    }
   *    return next.format(value);
   *   }
   * });
   *
   * </pre>
   *
   * @param formatter A formatter.
   * @return This handlebars object.
   */
  public Handlebars with(final Formatter formatter) {
    notNull(formatter, "A formatter is required.");

    formatters.add(formatter);

    this.formatter = new FormatterChain(formatters);

    return this;
  }

  /**
   * Set the handlebars.js location used it to compile/precompile template to JavaScript.
   * <p>
   * Using handlebars.js 4.x:
   * </p>
   *
   * <pre>
   *   Handlebars handlebars = new Handlebars()
   *      .handlebarsJsFile("handlebars-v4.0.4.js");
   * </pre>
   * <p>
   * Using handlebars.js 1.x:
   * </p>
   *
   * <pre>
   *   Handlebars handlebars = new Handlebars()
   *      .handlebarsJsFile("handlebars-v1.3.0.js");
   * </pre>
   *
   * Default handlebars.js is <code>handlebars-v4.0.4.js</code>.
   *
   * @param location A classpath location of the handlebar.js file.
   * @return This instance of Handlebars.
   */
  public Handlebars handlebarsJsFile(final String location) {
    this.handlebarsJsFile = notEmpty(location, "A handlebars.js location is required.");
    if (!this.handlebarsJsFile.startsWith("/")) {
      this.handlebarsJsFile = "/" + handlebarsJsFile;
    }
    URL resource = getClass().getResource(handlebarsJsFile);
    if (resource == null) {
      throw new IllegalArgumentException("File not found: " + handlebarsJsFile);
    }
    return this;
  }

  /**
   * @return Classpath location of the handlebars.js file. Default is:
   *         <code>handlebars-v4.0.4.js</code>
   */
  public String handlebarsJsFile() {
    return handlebarsJsFile;
  }

  /**
   * @return True, if we want to extend lookup to parent scope, like Mustache Spec. Or false, if
   *         lookup is restricted to current scope, like handlebars.js.
   */
  public boolean parentScopeResolution() {
    return parentScopeResolution;
  }

  /**
   * Given:
   * <pre>
   * {
   *   "value": "Brett",
   *   "child": {
   *      "bestQB" : "Favre"
   *    }
   * }
   * </pre>
   *
   * Handlebars.java will output: <code>Hello Favre Brett</code> while handlebars.js:
   * <code>Hello Favre</code>.
   *
   * Why? Handlebars.java is a 100% Mustache implementation while handlebars.js isn't.
   *
   * This option forces Handlebars.java mimics handlebars.js behavior:
   *
   * <pre>
   * Handlebars hbs = new Handlebars()
   *   .parentScopeResolution(true);
   * </pre>
   *
   * Outputs: <code>Hello Favre</code>.
   *
   *
   * @param parentScopeResolution False, if we want to restrict lookup to current scope (like in
   *        handlebars.js). Default is <code>true</code>
   */
  public void setParentScopeResolution(final boolean parentScopeResolution) {
    this.parentScopeResolution = parentScopeResolution;
  }

  /**
   * Given:
   * <pre>
   * {
   *   "value": "Brett",
   *   "child": {
   *      "bestQB" : "Favre"
   *    }
   * }
   * </pre>
   *
   * Handlebars.java will output: <code>Hello Favre Brett</code> while handlebars.js:
   * <code>Hello Favre</code>.
   *
   * Why? Handlebars.java is a 100% Mustache implementation while handlebars.js isn't.
   *
   * This option forces Handlebars.java mimics handlebars.js behavior:
   *
   * <pre>
   * Handlebars hbs = new Handlebars()
   *   .parentScopeResolution(true);
   * </pre>
   *
   * Outputs: <code>Hello Favre</code>.
   *
   *
   * @param parentScopeResolution False, if we want to restrict lookup to current scope (like in
   *        handlebars.js). Default is <code>true</code>
   * @return This handlebars.
   */
  public Handlebars parentScopeResolution(final boolean parentScopeResolution) {
    setParentScopeResolution(parentScopeResolution);
    return this;
  }

  /**
   * Return a parser factory.
   *
   * @return A parser factory.
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

  @Override
  public Decorator decorator(final String name) {
    return registry.decorator(name);
  }

  @Override
  public Handlebars registerDecorator(final String name, final Decorator decorator) {
    registry.registerDecorator(name, decorator);
    return this;
  }

}
