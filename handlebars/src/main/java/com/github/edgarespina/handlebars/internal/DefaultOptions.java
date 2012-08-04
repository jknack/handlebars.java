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
package com.github.edgarespina.handlebars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.github.edgarespina.handlebars.Context;
import com.github.edgarespina.handlebars.Handlebars;
import com.github.edgarespina.handlebars.Options;
import com.github.edgarespina.handlebars.Template;

/**
 * An implementation of {@link Options}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class DefaultOptions extends Options {

  /**
   * An empty template implementation.
   */
  private static Template EMPTY = new Template() {
    @Override
    public String text() {
      return "";
    }

    @Override
    public String apply(final Object context) throws IOException {
      return "";
    }

    @Override
    public String apply(final Context context) throws IOException {
      return "";
    }

    @Override
    public void apply(final Context context, final Writer writer)
        throws IOException {
    }

    @Override
    public void apply(final Object context, final Writer writer)
        throws IOException {
    }
  };

  /**
   * A thread safe storage.
   */
  private Map<String, Object> storage;

  /**
   * Creates a new {@link DefaultOptions}.
   *
   * @param handlebars The {@link Handlebars} object. Required.
   * @param fn The current template. Required.
   * @param inverse The current inverse template. Optional.
   * @param context The current context. Required.
   * @param params The parameters. Required.
   * @param hash The hash. Required.
   */
  public DefaultOptions(final Handlebars handlebars, final Template fn,
      final Template inverse, final Context context, final Object[] params,
      final Map<String, Object> hash) {
    super(handlebars, context, fn, inverse == null ? EMPTY : inverse, params,
        hash);
    storage = context.storage();
  }

  @Override
  public CharSequence fn() throws IOException {
    return fn(context);
  }

  @Override
  public CharSequence fn(final Object context) throws IOException {
    return applyIfPossible(fn, context);
  }

  @Override
  public CharSequence inverse() throws IOException {
    return inverse(context);
  }

  @Override
  public CharSequence inverse(final Object context) throws IOException {
    return applyIfPossible(inverse, context);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(final String name) {
    T value = (T) context.get(name);
    if (value == null) {
      value = (T) storage.get(name);
    }
    return value;
  }

  @Override
  public Template partial(final String path) {
    return partials().get(path);
  }

  @Override
  public void partial(final String path, final Template partial) {
    partials().put(path, partial);
  }

  /**
   * Apply the given template if the context object isn't null.
   *
   * @param template The template.
   * @param context The context object.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  private CharSequence applyIfPossible(final Template template,
      final Object context)
      throws IOException {
    if (context == null) {
      return "";
    }
    return apply(template, context);
  }

  @Override
  public CharSequence apply(final Template template) throws IOException {
    return apply(template, context);
  }

  @Override
  public CharSequence apply(final Template template, final Object context)
      throws IOException {

    final CharSequence result;
    if (context == this.context.model() || context == this.context
        || context instanceof Context) {
      // Same context or the param is a context already.
      result = template.apply(this.context);
    } else {
      // Expand the provided context.
      result = template.apply(Context.newContext(this.context, context));
    }
    return result;
  }

  /**
   * Return the partials storage.
   *
   * @return The partials storage.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Template> partials() {
    return (Map<String, Template>) storage.get(Context.PARTIALS);
  }

  /**
   * Cleanup resources.
   */
  public void destroy() {
    hash.clear();
    storage = null;
  }

}
