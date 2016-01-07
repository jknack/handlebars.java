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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
   * Buffer like use it to increase rendering time while using helpers.
   *
   * @author edgar
   * @since 2.3.2
   */
  public interface Buffer extends Appendable, CharSequence {
  }

  /**
   * This buffer will write into the underlying writer. It won't be any visible output and
   * {@link #toString()} returns an empty string.
   *
   * @author edgar
   * @since 2.3.2
   */
  public static class NativeBuffer implements Buffer {

    /** Writer. */
    private Writer writer;

    /**
     * Creates a new {@link NativeBuffer}.
     *
     * @param writer A writer. Required.
     */
    public NativeBuffer(final Writer writer) {
      this.writer = writer;
    }

    @Override
    public Appendable append(final CharSequence csq) throws IOException {
      writer.append(csq);
      return this;
    }

    @Override
    public Appendable append(final CharSequence csq, final int start, final int end)
        throws IOException {
      writer.append(csq, start, end);
      return this;
    }

    @Override
    public Appendable append(final char c) throws IOException {
      writer.append(c);
      return this;
    }

    @Override
    public int length() {
      // no need to merge anything
      return 0;
    }

    @Override
    public char charAt(final int index) {
      throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      // no need to merge anything
      return "";
    }
  }

  /**
   * A {@link StringBuilder} implementation.
   *
   * @author edgar
   * @since 2.3.2
   */
  public static class InMemoryBuffer implements Buffer {

    /** A buffer. */
    private StringBuilder buffer = new StringBuilder();

    @Override
    public Appendable append(final CharSequence csq) throws IOException {
      buffer.append(csq);
      return this;
    }

    @Override
    public Appendable append(final CharSequence csq, final int start, final int end)
        throws IOException {
      buffer.append(csq, start, end);
      return this;
    }

    @Override
    public Appendable append(final char c) throws IOException {
      buffer.append(c);
      return this;
    }

    @Override
    public int length() {
      return buffer.length();
    }

    @Override
    public char charAt(final int index) {
      return buffer.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
      return buffer.subSequence(start, end);
    }

  }

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

    /** Empty params. */
    private static Object[] EMPTY_PARAMS = {};

    /**
     * The parameters. Not null.
     */
    private Object[] params = EMPTY_PARAMS;

    /**
     * The hash options. Not null.
     */
    private Map<String, Object> hash = Collections.emptyMap();

    /**
     * The {@link TagType} from where the helper was called.
     */
    private TagType tagType;

    /** The name of the helper. */
    private String helperName;

    /** Output writer. */
    private Writer writer;

    /** Block params. */
    private List<String> blockParams = Collections.emptyList();

    /**
     * Creates a new {@link Builder}.
     *
     * @param handlebars A handlebars object. Required.
     * @param helperName The name of the helper. Required.
     * @param tagType The {@link TagType} from where the helper was called.
     * @param context A context object. Required.
     * @param fn A template object. Required.
     */
    public Builder(final Handlebars handlebars, final String helperName, final TagType tagType,
        final Context context, final Template fn) {
      this.handlebars = handlebars;
      this.helperName = helperName;
      this.tagType = tagType;
      this.context = context;
      this.fn = fn;
    }

    /**
     * Build a new {@link Options} object.
     *
     * @return A new {@link Options} object.
     */
    public Options build() {
      Options options = new Options(handlebars, helperName, tagType, context, fn, inverse, params,
          hash, blockParams);
      options.writer = writer;
      // clear out references
      handlebars = null;
      tagType = null;
      context = null;
      fn = null;
      inverse = null;
      params = null;
      hash = null;
      writer = null;
      return options;
    }

    /**
     * Set the options hash.
     *
     * @param hash A hash table. Required.
     * @return This builder.
     */
    public Builder setHash(final Map<String, Object> hash) {
      this.hash = hash;
      return this;
    }

    /**
     * Set the options block params.
     *
     * @param blockParams A block params. Required.
     * @return This builder.
     */
    public Builder setBlockParams(final List<String> blockParams) {
      this.blockParams = blockParams;
      return this;
    }

    /**
     * Set the inverse template.
     *
     * @param inverse Inverse template. Required.
     * @return This builder.
     */
    public Builder setInverse(final Template inverse) {
      this.inverse = inverse;
      return this;
    }

    /**
     * Set the options parameters.
     *
     * @param params A parameters list. Required.
     * @return This builder.
     */
    public Builder setParams(final Object[] params) {
      this.params = params;
      return this;
    }

    /**
     * Set a writer, useful to improve performance.
     *
     * @param writer A writer. Required.
     * @return This builder.
     */
    public Builder setWriter(final Writer writer) {
      this.writer = writer;
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
   * The {@link TagType} from where the helper was called.
   */
  public final TagType tagType;

  /** The name of the helper. */
  public final String helperName;

  /** Output writer. */
  private Writer writer;

  /** Block param names. */
  public final List<String> blockParams;

  /** True, if there is any block param. */
  private boolean hasBlockParams;

  /**
   * Creates a new Handlebars {@link Options}.
   *
   * @param handlebars The handlebars instance. Required.
   * @param helperName The name of the helper. Required.
   * @param tagType The {@link TagType} from where the helper was called.
   * @param context The current context. Required.
   * @param fn The template function. Required.
   * @param inverse The inverse template function. Required.
   * @param params The parameters. Required.
   * @param hash The optional hash. Required.
   * @param blockParams The block param names. Required.
   */
  public Options(final Handlebars handlebars, final String helperName, final TagType tagType,
      final Context context, final Template fn, final Template inverse, final Object[] params,
      final Map<String, Object> hash, final List<String> blockParams) {
    this.handlebars = handlebars;
    this.helperName = helperName;
    this.tagType = tagType;
    this.context = context;
    this.fn = fn;
    this.inverse = inverse;
    this.params = params;
    this.hash = hash;
    this.blockParams = blockParams;
    hasBlockParams = this.blockParams.size() > 0;
  }

  /**
   * Creates a new Handlebars {@link Options}.
   *
   * @param handlebars The handlebars instance. Required.
   * @param helperName The name of the helper. Required.
   * @param tagType The {@link TagType} from where the helper was called.
   * @param context The current context. Required.
   * @param fn The template function. Required.
   * @param inverse The inverse template function. Required.
   * @param params The parameters. Required.
   * @param hash The optional hash. Required.
   * @param blockParams The block param names. Required.
   * @param writer A writer. Optional.
   */
  public Options(final Handlebars handlebars, final String helperName, final TagType tagType,
      final Context context, final Template fn, final Template inverse, final Object[] params,
      final Map<String, Object> hash, final List<String> blockParams, final Writer writer) {
    this.handlebars = handlebars;
    this.helperName = helperName;
    this.tagType = tagType;
    this.context = context;
    this.fn = fn;
    this.inverse = inverse;
    this.params = params;
    this.hash = hash;
    this.blockParams = blockParams;
    this.writer = writer;
    hasBlockParams = this.blockParams.size() > 0;
  }

  /**
   * Apply the {@link #fn} template using the default context.
   *
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence fn() throws IOException {
    return apply(fn, context, blockParams(context.model));
  }

  /**
   * Apply the {@link #fn} template using the provided context.
   *
   * @param context The context to use.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence fn(final Object context) throws IOException {
    Context ctx = wrap(context);
    return apply(fn, ctx, blockParams(ctx.model));
  }

  /**
   * Apply the {@link #fn} template using the provided context.
   *
   * @param context The context to use.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence fn(final Context context) throws IOException {
    Context ctx = wrap(context);
    return apply(fn, ctx, blockParams(ctx.model));
  }

  /**
   * Apply the {@link #inverse} template using the default context.
   *
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence inverse() throws IOException {
    return apply(inverse, context, blockParams(context.model));
  }

  /**
   * Apply the {@link #inverse} template using the provided context.
   *
   * @param context The context to use.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence inverse(final Object context) throws IOException {
    Context ctx = wrap(context);
    return apply(inverse, ctx, blockParams(ctx.model));
  }

  /**
   * Apply the {@link #inverse} template using the provided context.
   *
   * @param context The context to use.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence inverse(final Context context) throws IOException {
    Context ctx = wrap(context);
    return apply(inverse, ctx, blockParams(ctx.model));
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
    Context ctx = wrap(context);
    return apply(template, ctx, blockParams(ctx.model));
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
  public CharSequence apply(final Template template, final Context context) throws IOException {
    Context ctx = wrap(context);
    return apply(template, ctx, blockParams(ctx.model));
  }

  /**
   * Apply the given template to the provided context. The context stack is
   * propagated allowing the access to the whole stack.
   *
   * @param template The template.
   * @param context The context object.
   * @param blockParams The block param values.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence apply(final Template template, final Context context,
      final List<Object> blockParams) throws IOException {
    Context ctx = context;
    if (hasBlockParams) {
      ctx = Context.newBlockParamContext(context, this.blockParams, blockParams);
    }
    return template.apply(ctx);
  }

  /**
   * Apply the given template to the provided context. The context stack is
   * propagated allowing the access to the whole stack.
   *
   * @param template The template.
   * @param context The context object.
   * @param blockParams The block param values.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  public CharSequence apply(final Template template, final Object context,
      final List<Object> blockParams) throws IOException {
    return apply(template, wrap(context), blockParams);
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
    return apply(template, context, blockParams(context.model));
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
    if (model == context.model || model == context) {
      return context;
    }
    if (model instanceof Context) {
      return (Context) model;
    }
    return Context.newContext(context, model);
  }

  /**
   * Creates a {@link Context} from the given model. If the object is a context
   * already the same object will be returned.
   *
   * @param context The model object.
   * @return A context representing the model or the same model if it's a
   *         context already.
   */
  private Context wrap(final Context context) {
    if (context != null) {
      return context;
    }
    return Context.newContext(null);
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

  /**
   * Get a Buffer which probably increase rendering time (performance). Usage:
   *
   * <pre>
   * public CharSequence helper(Object ctx, Options options) {
   *   Buffer buffer = options.buffer();
   *   ...
   *   buffer.append(...);
   *   ...
   *   return buffer;
   * }
   * </pre>
   *
   * Something to keep in mind is that when using the native buffer there won't be any visible
   * output. For example {@link NativeBuffer#toString()} results in an empty string, that's expected
   * because the content is written directly to the underlying writer.
   *
   * @return A new buffer.
   */
  public Buffer buffer() {
    return writer == null ? new InMemoryBuffer() : new NativeBuffer(writer);
  }

  /**
   * Build block params from given context.
   *
   * @param context A context.
   * @return A block params.
   */
  private List<Object> blockParams(final Object context) {
    if (this.blockParams.size() == 1) {
      return Arrays.<Object> asList(context);
    }
    return Collections.emptyList();
  }
}
