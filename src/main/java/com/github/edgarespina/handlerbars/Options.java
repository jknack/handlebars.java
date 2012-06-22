package com.github.edgarespina.handlerbars;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;

/**
 * Options available for {@link Helper#apply(Object, Options)}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public abstract class Options {

  /**
   * The {@link Handlebars} object. Not null.
   */
  public final Handlebars handlebars;

  /**
   * The current template. Not null.
   */
  public final Template fn;

  /**
   * The current inverse template. Not null.
   */
  public final Template inverse;

  /**
   * The parameters. Not null.
   */
  public final Object[] params;

  /**
   * The hash options. Not null.
   */
  public final Map<String, Object> hash;

  /**
   * Creates a new Handlebars {@link Options}.
   *
   * @param handlebars The handlebars instance. Required.
   * @param fn The template function. Required.
   * @param inverse The inverse template function. Required.
   * @param params The parameters. Required.
   * @param hash The optional hash. Required.
   */
  public Options(final Handlebars handlebars, final Template fn,
      final Template inverse, final Object[] params,
      final Map<String, Object> hash) {
    this.handlebars = checkNotNull(handlebars, "The handlebars is required.");
    this.fn = checkNotNull(fn, "The template is required.");
    this.inverse = checkNotNull(inverse, "The inverse template is required.");
    this.params = checkNotNull(params, "The parameters are required.");
    this.hash = checkNotNull(hash, "The hash are required.");
  }

  /**
   * Apply the {@link #fn} template using the default context.
   *
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public abstract CharSequence fn() throws IOException;

  /**
   * Apply the {@link #fn} template using the provided context.
   *
   * @param context The context to use.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public abstract CharSequence fn(Object context) throws IOException;

  /**
   * Apply the {@link #inverse} template using the default context.
   *
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public abstract CharSequence inverse() throws IOException;

  /**
   * Apply the {@link #inverse} template using the provided context.
   *
   * @param context The context to use.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public abstract CharSequence inverse(Object context) throws IOException;

  /**
   * Apply the given template to the provided context. The context stack is
   * propagated allowing the access to the whole stack.
   *
   * @param template The template.
   * @param context The context object.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public abstract CharSequence apply(final Template template,
      final Object context) throws IOException;

  /**
   * Apply the given template to the default context. The context stack is
   * propagated allowing the access to the whole stack.
   *
   * @param template The template.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public abstract CharSequence apply(final Template template)
      throws IOException;

  /**
   * <p>
   * Return a parameter at given index. This is analogous to:
   * </p>
   * <code>
   *  Object param = options.params[index]
   * </code>
   * <p>
   * The only difference is the type safe feature:
   * </p>
   * <code>
   *  MyType param = options.param(index)
   * </code>
   *
   * @param <T> The runtime type.
   * @param index The parameter position.
   * @return The paramater's value.
   */
  @SuppressWarnings("unchecked")
  public final <T> T param(final int index) {
    return (T) params[index];
  }

  /**
   * <p>
   * Return a parameter at given index. This is analogous to:
   * </p>
   * <code>
   *  Object param = options.params[index]
   * </code>
   * <p>
   * The only difference is the type safe feature:
   * </p>
   * <code>
   *  MyType param = options.param(index)
   * </code>
   *
   * @param <T> The runtime type.
   * @param index The parameter position.
   * @param defaultValue The default value to return if the parameter is not
   *        present or if null.
   * @return The paramater's value.
   */
  @SuppressWarnings("unchecked")
  public final <T> T param(final int index, final T defaultValue) {
    T value = null;
    if (index < params.length) {
      value = (T) params[index];
    }
    return value == null ? defaultValue : value;
  }

  /**
   * Look for a value in the context's stack.
   *
   * @param <T> The runtime type.
   * @param name The property's name.
   * @return The associated value or <code>null</code> if it's not found.
   */
  public abstract <T> T get(String name);

  /**
   * Return a previously registered partial in the current execution context.
   *
   * @param path The partial's path. Required.
   * @return A previously registered partial in the current execution context.
   *         Or <code> null</code> if not found.
   */
  public abstract Template partial(String path);

  /**
   * Store a partial in the current execution context.
   *
   * @param path The partial's path. Required.
   * @param partial The partial template. Required.
   */
  public abstract void partial(String path, Template partial);

  /**
   * <p>
   * Find a value inside the {@link #hash} attributes. This is analogous to:
   * </p>
   * <code>
   *  Object myClass = options.hash.get("class");
   * </code>
   * <p>
   * This mehtod works as a shorthand and type safe call:
   * </p>
   * <code>
   *  String myClass = options.hash("class");
   * </code>
   *
   * @param <T> The runtime type.
   * @param name The hash's name.
   * @return The hash value or null.
   */
  public final <T> T hash(final String name) {
    return hash(name, null);
  }

  /**
   * <p>
   * Find a value inside the {@link #hash} attributes. This is analogous to:
   * </p>
   * <code>
   *  Object myClass = options.hash.get("class");
   * </code>
   * <p>
   * This mehtod works as a shorthand and type safe call:
   * </p>
   * <code>
   *  String myClass = options.hash("class");
   * </code>
   *
   * @param <T> The runtime type.
   * @param name The hash's name.
   * @param defaultValue The default value to returns.
   * @return The hash value or null.
   */
  @SuppressWarnings("unchecked")
  public final <T> T hash(final String name, final Object defaultValue) {
    Object value = hash.get(name);
    return (T) (value == null ? defaultValue : value);
  }

  /**
   * Returns false if its argument is false, null or empty list/array (a "falsy"
   * value).
   *
   * @param value A value.
   * @return False if its argument is false, null or empty list/array (a "falsy"
   *         value).
   */
  public final boolean isEmpty(final Object value) {
    return Handlebars.Utils.isEmpty(value);
  }
}
