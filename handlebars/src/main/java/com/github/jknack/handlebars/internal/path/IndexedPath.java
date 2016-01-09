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
package com.github.jknack.handlebars.internal.path;

import java.lang.reflect.Array;
import java.util.List;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.PathExpression;
import com.github.jknack.handlebars.ValueResolver;

/**
 * Resolve array or collection like access, but also invalid Java ID.
 *
 * @author edgar
 * @since 4.0.1
 */
public class IndexedPath implements PathExpression {

  /** Index . */
  private int idx;

  /** Property path. */
  private String name;

  /** True, if we want to restrict lookup to current scope. */
  private boolean local;

  /**
   * A new {@link IndexedPath}.
   *
   * @param idx Index.
   * @param name Path.
   * @param local True, if we want to restrict lookup to current scope.
   */
  public IndexedPath(final int idx, final String name, final boolean local) {
    this.idx = idx;
    this.name = name;
    this.local = local;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object eval(final ValueResolver resolver, final Context context, final Object data,
      final Chain chain) {
    try {
      Object value = data;
      if (data instanceof List) {
        value = ((List) value).get(idx);
      } else if (value.getClass().isArray()) {
        value = Array.get(value, idx);
      } else {
        // fallback, invalid ID
        value = resolver.resolve(value, name);
      }
      return chain.next(resolver, context, value);
    } catch (IndexOutOfBoundsException exception) {
      // Index is outside of range, fallback to null as in handlebar.js
      return null;
    }
  }

  @Override
  public boolean local() {
    return local;
  }

  @Override
  public String toString() {
    return "[" + idx + "]";
  }
}
