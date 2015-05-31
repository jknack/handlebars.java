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
package com.github.jknack.handlebars.helper;

import java.io.IOException;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Lookup helper, which allows to get a context variable... It is kind of useless, but it it present
 * to keep better integration with handlebars.js.
 *
 * It was introduced with dynamic partials:
 *
 * <pre>
 * {{> (lookup '.' 'myVariable') }}
 * </pre>
 *
 * This helper is useless bc it shouldn't be required to get a variable via a helper, like:
 *
 * <pre>
 * {{> (myVariable) }}
 * </pre>
 *
 * For now, previous expression isn't supported in Handlebars.java... but the only reason of that is
 * handlebars.js
 *
 * @author edgar
 * @since 2.2.0
 */
public class LookupHelper implements Helper<Object> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<Object> INSTANCE = new LookupHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "lookup";

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    if (options.params.length <= 0) {
      return context.toString();
    }
    Context ctx = options.context;
    while (ctx != null && context != ctx.model()) {
      ctx = ctx.parent();
    }
    if (ctx == null) {
      return null;
    }
    Object lookup = ctx.get(options.param(0).toString());
    if (lookup == null) {
      return null;
    }
    return lookup.toString();
  }

}
