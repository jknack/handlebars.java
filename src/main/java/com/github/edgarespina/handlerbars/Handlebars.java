package com.github.edgarespina.handlerbars;

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

import com.github.edgarespina.handlerbars.internal.Parser;
import com.github.edgarespina.handlerbars.io.ClasspathLocator;

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
public final class Handlebars {

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
    private String content;

    /**
     * Creates a new {@link SafeString}.
     *
     * @param content The string content.
     */
    public SafeString(final String content) {
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
      return content;
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
      if (value instanceof Array) {
        return Array.getLength(value) == 0;
      }
      if (value instanceof Collection) {
        return ((Collection) value).size() == 0;
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
   * The logging system.
   */
  private static final Logger logger = getLogger(Handlebars.class);

  /**
   * The resource locator. Required.
   */
  private final ResourceLocator<?> resourceLocator;

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
   * @param resourceLocator The resource locator. Required.
   */
  public Handlebars(final ResourceLocator<?> resourceLocator) {
    this.resourceLocator =
        checkNotNull(resourceLocator, "The resource locator is required.");
    BuiltInHelpers.register(this);
  }

  /**
   * Creates a new {@link Handlebars} with a classpath resource locator.
   */
  public Handlebars() {
    this(new ClasspathLocator());
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
    Reader reader = resourceLocator.locate(uri);
    return Parser.create(this, startDelimiter, endDelimiter).parse(reader);
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
   * @param input The resource's input. Required.
   * @param startDelimiter The start delimiter. Required.
   * @param endDelimiter The end delimiter. Required.
   * @return A compiled template.
   * @throws IOException If the resource cannot be loaded.
   */
  public Template compile(final String input, final String startDelimiter,
      final String endDelimiter) throws IOException {
    return Parser.create(this, startDelimiter, endDelimiter).parse(input);
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
   * The resource locator.
   *
   * @return The resource locator.
   */
  public ResourceLocator<?> getResourceLocator() {
    return resourceLocator;
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
    logger.warn(String.format(message, args));
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
    logger.debug(String.format(message, args));
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
}
