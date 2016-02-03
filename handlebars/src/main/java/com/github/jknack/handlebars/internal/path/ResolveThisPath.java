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
import com.github.jknack.handlebars.ValueResolver;

/**
 * Resolve a this/current path, like <code>.</code>,<code>this</code> or <code>./</code>.
 *
 * @author edgar
 * @since 4.0.1
 */
public class ResolveThisPath extends ThisPath {


  /**
   * Creates a new path resolver.
   *
   * @param name A this path.
   */
  public ResolveThisPath(final String name) {
    super(name);
  }

  @Override
  public Object eval(final ValueResolver resolver, final Context context, final Object data,
      final Chain chain) {
    Object value = resolver.resolve(data);
    return value == null ? data : value;
  }


}
