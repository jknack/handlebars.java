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

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.PathExpression;
import com.github.jknack.handlebars.ValueResolver;

/**
 * Resolve <code>@data</code> expression by lookup whole/complete name first and then without the at
 * symbol.
 *
 * @author edgar
 * @since 4.0.1
 */
public class DataPath implements PathExpression {

  /** Property name. */
  private String name;

  /** Property name without @. */
  private String nameWithoutAtSymbol;

  /**
   * Creates a new {@link DataPath} expression.
   *
   * @param name Expression name.
   */
  public DataPath(final String name) {
    this.name = name;
    this.nameWithoutAtSymbol = name.substring(1);
  }

  @Override
  public Object eval(final ValueResolver resolver, final Context context, final Object data,
      final Chain chain) {
    // with @
    Object value = resolver.resolve(data, name);
    if (value == null) {
      // without @
      value = resolver.resolve(data, nameWithoutAtSymbol);
    }
    return chain.next(resolver, context, value);
  }

  @Override
  public boolean local() {
    return false;
  }

  @Override
  public String toString() {
    return name;
  }
}
