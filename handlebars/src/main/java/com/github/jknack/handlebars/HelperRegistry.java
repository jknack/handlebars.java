/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The helper registry.
 *
 * @author edgar
 * @since 1.2.0
 */
public interface HelperRegistry {

  /** The missing helper's name. */
  String HELPER_MISSING = "helperMissing";

  /**
   * Find a helper by name.
   *
   * @param <C> The helper runtime type.
   * @param name The helper's name. Required.
   * @return A helper or null if it's not found.
   */
  <C> Helper<C> helper(String name);

  /**
   * List all the helpers from registry.
   *
   * @return Available helpers in the registry.
   */
  Set<Entry<String, Helper<?>>> helpers();

  /**
   * Register a helper in the helper registry.
   *
   * @param <H> The helper runtime type.
   * @param name The helper's name. Required.
   * @param helper The helper object. Required.
   * @return This handlebars.
   */
  <H> HelperRegistry registerHelper(String name, Helper<H> helper);

  /**
   * Register the special helper missing in the registry.
   *
   * @param <H> The helper runtime type.
   * @param helper The helper object. Required.
   * @return This handlebars.
   */
  <H> HelperRegistry registerHelperMissing(Helper<H> helper);

  /**
   * Register all the helper methods for the given helper source.
   *
   * <p>A helper method looks like:
   *
   * <pre>
   * public static? CharSequence methodName(context?, parameter*, options?) {
   * }
   * </pre>
   *
   * Where:
   *
   * <ul>
   *   <li>A method can/can't be static
   *   <li>The method's name became the helper's name
   *   <li>Context, parameters and options are all optional
   *   <li>If context and options are present they must be the first and last method arguments.
   * </ul>
   *
   * Instance and static methods will be registered as helpers.
   *
   * @param helperSource The helper source. Required.
   * @return This handlebars object.
   */
  HelperRegistry registerHelpers(Object helperSource);

  /**
   * Register all the helper methods for the given helper source.
   *
   * <p>A helper method looks like:
   *
   * <pre>
   * public static? CharSequence methodName(context?, parameter*, options?) {
   * }
   * </pre>
   *
   * Where:
   *
   * <ul>
   *   <li>A method can/can't be static
   *   <li>The method's name became the helper's name
   *   <li>Context, parameters and options are all optional
   *   <li>If context and options are present they must be the first and last method arguments.
   * </ul>
   *
   * Only static methods will be registered as helpers.
   *
   * <p>Enums are supported too
   *
   * @param helperSource The helper source. Enums are supported. Required.
   * @return This handlebars object.
   */
  HelperRegistry registerHelpers(Class<?> helperSource);

  /**
   * Register helpers from a JavaScript source.
   *
   * <p>A JavaScript source file looks like:
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
  HelperRegistry registerHelpers(URI location) throws Exception;

  /**
   * Register helpers from a JavaScript source.
   *
   * <p>A JavaScript source file looks like:
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
  HelperRegistry registerHelpers(File input) throws Exception;

  /**
   * Register helpers from a JavaScript source.
   *
   * <p>A JavaScript source file looks like:
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
  HelperRegistry registerHelpers(String filename, Reader source) throws Exception;

  /**
   * Register helpers from a JavaScript source.
   *
   * <p>A JavaScript source file looks like:
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
  HelperRegistry registerHelpers(String filename, InputStream source) throws Exception;

  /**
   * Register helpers from a JavaScript source.
   *
   * <p>A JavaScript source file looks like:
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
   * @return This registry.
   * @throws IOException If the JavaScript helpers can't be registered.
   */
  HelperRegistry registerHelpers(String filename, String source) throws IOException;

  /**
   * Find a decorator by name.
   *
   * @param name A decorator's name.
   * @return A decorator or <code>null</code>.
   * @since 4.0.0
   */
  Decorator decorator(String name);

  /**
   * Register a decorator and make it accessible via {@link #decorator(String)}.
   *
   * @param name A decorator's name. Required.
   * @param decorator A decorator. Required.
   * @return This registry.
   * @since 4.0.0
   */
  HelperRegistry registerDecorator(String name, Decorator decorator);

  /**
   * Set the charset to use.
   *
   * @param charset Charset.
   * @return This registry.
   * @since 4.0.6
   */
  HelperRegistry setCharset(Charset charset);
}
