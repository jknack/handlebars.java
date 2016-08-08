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
package com.github.jknack.handlebars.helper;

import java.io.IOException;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Lookup helper, which allows to get a context variable.
 *
 * It was introduced with dynamic partials:
 *
 * <pre>
 * {{> (lookup '.' 'myVariable') }}
 * </pre>
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
  public Object apply(final Object context, final Options options)
      throws IOException {
    if (context == null) {
      return null;
    }
    if (options.params.length <= 0) {
      return context;
    }
    Context ctx = Context.newBuilder(options.context, context).build();
    Object lookup = ctx.get(options.param(0).toString());
    if (lookup == null) {
      return context;
    }
    return lookup;
  }

}
