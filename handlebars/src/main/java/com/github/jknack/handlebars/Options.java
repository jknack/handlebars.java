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

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Options available for {@link Helper#apply(Object, Options)}.
 * Usage:
 *
 * <pre>
 *   Options options = new Options.Builder(handlebars, context, fn)
 *      .build();
 * </pre>
 *
 * Optionally you can set parameters and hash table:
 *
 * <pre>
 *   Options options = new Options.Builder(handlebars, context, fn)
 *      .setParams(new Object[] {})
 *      .setHash(hash)
 *      .build();
 * </pre>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class Options {

  /**
   * An {@link Options} builder.
   *
   * @author edgar.espina
   * @since 0.9.0
   */
  public static class Builder {
    /**
     * The {@link Handlebars} object. Not null.
     */
    private Handlebars handlebars;

    /**
     * The current context. Not null.
     */
    private Context context;

    /**
     * The current template. Not null.
     */
    private Template fn;

    /**
     * The current inverse template. Not null.
     */
    private Template inverse = Template.EMPTY;

    /**
     * The parameters. Not null.
     */
    private Object[] params = {};

    /**
     * The hash options. Not null.
     */
    private Map<String, Object> hash = Collections.emptyMap();

    /**
     * Creates a new {@link Builder}.
     *
     * @param handlebars A handlebars object. Required.
     * @param context A context object. Required.
     * @param fn A template object. Required.
     */
    public Builder(final Handlebars handlebars, final Context context, final Template fn) {
      this.handlebars = notNull(handlebars, "The handlebars is required.");
      this.context = notNull(context, "The context is required.");
      this.fn = notNull(fn, "The fn template is required.");
    }

    /**
     * Build a new {@link Options} object.
     *
     * @return A new {@link Options} object.
     */
    public Options build() {
      Options options = new Options(handlebars, context, fn, inverse, params, hash);
      // clear out references
      handlebars = null;
      context = null;
      fn = null;
      inverse = null;
      params = null;
      hash = null;
      return options;
    }

    /**
     * Set the options hash.
     *
     * @param hash A hash table. Required.
     * @return This builder.
     */
    public Builder setHash(final Map<String, Object> hash) {
      this.hash = notNull(hash, "The hash is required.");
      return this;
    }

    /**
     * Set the inverse template.
     *
     * @param inverse Inverse template. Required.
     * @return This builder.
     */
    public Builder setInverse(final Template inverse) {
      this.inverse = notNull(inverse, "The inverse is required.");
      return this;
    }

    /**
     * Set the options parameters.
     *
     * @param params A parameters list. Required.
     * @return This builder.
     */
    public Builder setParams(final Object[] params) {
      this.params = notNull(params, "The params is required.");
      return this;
    }
  }

  /**
   * The {@link Handlebars} object. Not null.
   */
  public final Handlebars handlebars;

  /**
   * The current context. Not null.
   */
  public final Context context;

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
   * @param context The current context. Required.
   * @param fn The template function. Required.
   * @param inverse The inverse template function. Required.
   * @param params The parameters. Required.
   * @param hash The optional hash. Required.
   */
  public Options(final Handlebars handlebars, final Context context,
      final Template fn, final Template inverse, final Object[] params,
      final Map<String, Object> hash) {
    this.handlebars = notNull(handlebars, "The handlebars is required.");
    this.context = notNull(context, "The context is required");
    this.fn = notNull(fn, "The template is required.");
    this.inverse = notNull(inverse, "The inverse template is required.");
    this.params = notNull(params, "The parameters are required.");
    this.hash = notNull(hash, "The hash are required.");
  }

  /**
   * Apply the {@link #fn} template using the default context.
   *
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence fn() throws IOException {
    return fn(context);
  }

  /**
   * Apply the {@link #fn} template using the provided context.
   *
   * @param context The context to use.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence fn(final Object context) throws IOException {
    return apply(fn, context);
  }

  /**
   * Apply the {@link #inverse} template using the default context.
   *
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence inverse() throws IOException {
    return inverse(context);
  }

  /**
   * Apply the {@link #inverse} template using the provided context.
   *
   * @param context The context to use.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence inverse(final Object context) throws IOException {
    return apply(inverse, context);
  }

  /**
   * Apply the given template to the provided context. The context stack is
   * propagated allowing the access to the whole stack.
   *
   * @param template The template.
   * @param context The context object.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence apply(final Template template, final Object context) throws IOException {
    return template.apply(wrap(context));
  }

  /**
   * Apply the given template to the default context. The context stack is
   * propagated allowing the access to the whole stack.
   *
   * @param template The template.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence apply(final Template template) throws IOException {
    return apply(template, context);
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
   * @return The paramater's value.
   */
  @SuppressWarnings("unchecked")
  public <T> T param(final int index) {
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
  public <T> T param(final int index, final T defaultValue) {
    T value = null;
    if (index >= 0 && index < params.length) {
      value = (T) params[index];
    }
    return value == null ? defaultValue : value;
  }

  /**
   * Look for a value in the context's stack.
   *
   * @param <T> The runtime type.
   * @param name The property's name.
   * @param defaultValue The default value to return if the attribute is not
   *        present or if null.
   * @return The associated value or <code>null</code> if it's not found.
   */
  public <T> T get(final String name, final T defaultValue) {
    @SuppressWarnings("unchecked")
    T value = (T) context.get(name);
    return value == null ? defaultValue : value;
  }

  /**
   * Look for a value in the context's stack.
   *
   * @param <T> The runtime type.
   * @param name The property's name.
   * @return The associated value or <code>null</code> if it's not found.
   */
  public <T> T get(final String name) {
    return get(name, null);
  }

  /**
   * Return a previously registered partial in the current execution context.
   *
   * @param path The partial's path. Required.
   * @return A previously registered partial in the current execution context.
   *         Or <code> null</code> if not found.
   */
  public Template partial(final String path) {
    return partials().get(path);
  }

  /**
   * Store a partial in the current execution context.
   *
   * @param path The partial's path. Required.
   * @param partial The partial template. Required.
   */
  public void partial(final String path, final Template partial) {
    partials().put(path, partial);
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
   * @return The hash value or null.
   */
  public <T> T hash(final String name) {
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
   * This method works as a shorthand and type safe call:
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
  public <T> T hash(final String name, final Object defaultValue) {
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
  public boolean isFalsy(final Object value) {
    return Handlebars.Utils.isEmpty(value);
  }

  /**
   * Creates a {@link Context} from the given model. If the object is a context
   * already the same object will be returned.
   *
   * @param model The model object.
   * @return A context representing the model or the same model if it's a
   *         context already.
   */
  public Context wrap(final Object model) {
    if (model == context) {
      return context;
    }
    if (model == context.model()) {
      return context;
    }
    if (model instanceof Context) {
      return (Context) model;
    }
    return Context.newContext(context, model);
  }

  /**
   * Read the attribute from the data storage.
   *
   * @param name The attribute's name.
   * @param <T> Data type.
   * @return The attribute value or null.
   */
  public <T> T data(final String name) {
    return context.data(name);
  }

  /**
   * Set an attribute in the data storage.
   *
   * @param name The attribute's name. Required.
   * @param value The attribute's value. Required.
   */
  public void data(final String name, final Object value) {
    context.data(name, value);
  }

  /**
   * List all the properties and their values for the given object.
   *
   * @param context The context object. Not null.
   * @return All the properties and their values for the given object.
   */
  public Set<Entry<String, Object>> propertySet(final Object context) {
    return this.context.propertySet(context instanceof Context
        ? ((Context) context).model()
        : context);
  }

  /**
   * Return the partials storage.
   *
   * @return The partials storage.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Template> partials() {
    return (Map<String, Template>) data(Context.PARTIALS);
  }

}
