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

import java.io.IOException;
import java.io.Writer;

/**
 * A compiled template created by {@link Handlebars#compile(String)}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public interface Template {

  /**
   * An empty template implementation.
   */
  Template EMPTY = new Template() {
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

    @Override
    public String toJavaScript() {
      return "";
    }

    @SuppressWarnings({"rawtypes", "unchecked" })
    @Override
    public <T> TypeSafeTemplate<T> as() {
      TypeSafeTemplate template = as(TypeSafeTemplate.class);
      return template;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, S extends TypeSafeTemplate<T>> S as(final Class<S> rootType) {
      TypeSafeTemplate<T> template = new TypeSafeTemplate<T>() {
        @Override
        public String apply(final T context) throws IOException {
          return "";
        }

        @Override
        public void apply(final T context, final Writer writer) throws IOException {
        }
      };
      return (S) template;
    }
  };

  /**
   * Merge the template tree using the given context.
   *
   * @param context The context object. May be null.
   * @param writer The writer object. Required.
   * @throws IOException If a resource cannot be loaded.
   */
  void apply(Object context, Writer writer) throws IOException;

  /**
   * Merge the template tree using the given context.
   *
   * @param context The context object. May be null.
   * @return The resulting template.
   * @throws IOException If a resource cannot be loaded.
   */
  String apply(Object context) throws IOException;

  /**
   * Merge the template tree using the given context.
   *
   * @param context The context object. Required.
   * @param writer The writer object. Required.
   * @throws IOException If a resource cannot be loaded.
   */
  void apply(Context context, Writer writer) throws IOException;

  /**
   * Merge the template tree using the given context.
   *
   * @param context The context object. Required.
   * @return The resulting template.
   * @throws IOException If a resource cannot be loaded.
   */
  String apply(Context context) throws IOException;

  /**
   * Provide the raw text.
   *
   * @return The raw text.
   */
  String text();

  /**
   * Return a pre-compiled JavaScript version of this template.
   *
   * @return A pre-compiled JavaScript version of this template.
   * @throws IOException If compilation fails.
   */
  String toJavaScript() throws IOException;

  /**
   * Creates a new {@link TypeSafeTemplate}.
   *
   * @param type The template type. Required.
   * @param <T> The root type.
   * @param <S> The template type.
   * @return A new {@link TypeSafeTemplate}.
   */
  <T, S extends TypeSafeTemplate<T>> S as(final Class<S> type);

  /**
   * Creates a new {@link TypeSafeTemplate}.
   *
   * @param <T> The root type.
   * @return A new {@link TypeSafeTemplate}.
   */
  <T> TypeSafeTemplate<T> as();
}
