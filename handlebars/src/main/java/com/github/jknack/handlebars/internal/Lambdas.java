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
package com.github.jknack.handlebars.internal;

import java.io.IOException;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Lambda;
import com.github.jknack.handlebars.Template;

/**
 * Utilities function for work with lambdas.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
final class Lambdas {

  /**
   * Not allowed.
   */
  private Lambdas() {
  }

  /**
   * Merge the lambda result.
   *
   * @param handlebars The handlebars.
   * @param lambda The lambda.
   * @param scope The current scope.
   * @param template The current template.
   * @return The resulting text.
   * @throws IOException If the resource cannot be loaded.
   */
  public static CharSequence merge(final Handlebars handlebars,
      final Lambda<Object, Object> lambda, final Context scope,
      final Template template) throws IOException {
    Template result = compile(handlebars, lambda, scope, template);
    return result.apply(scope);
  }

  /**
   * Compile the given lambda.
   *
   * @param handlebars The handlebars.
   * @param lambda The lambda.
   * @param scope The current scope.
   * @param template The template.
   * @return The resulting template.
   * @throws IOException If the resource cannot be loaded.
   */
  public static Template compile(final Handlebars handlebars,
      final Lambda<Object, Object> lambda, final Context scope,
      final Template template)
      throws IOException {
    return compile(handlebars, lambda, scope, template, "{{", "}}");
  }

  /**
   * Compile the given lambda.
   *
   * @param handlebars The handlebars.
   * @param lambda The lambda.
   * @param scope The current scope.
   * @param template The template.
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   * @return The resulting template.
   * @throws IOException If the resource cannot be loaded.
   */
  public static Template compile(final Handlebars handlebars,
      final Lambda<Object, Object> lambda, final Context scope,
      final Template template, final String startDelimiter,
      final String endDelimiter)
      throws IOException {
    Object value = lambda.apply(scope, template);
    final Template result;
    if (value instanceof CharSequence) {
      result = handlebars.compileInline(value.toString(), startDelimiter, endDelimiter);
    } else {
      // Don't escape no string values.
      result = new Text(handlebars, value.toString());
    }
    return result;
  }
}
