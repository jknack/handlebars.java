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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

/**
 * You can iterate over a list using the built-in each helper. Inside the
 * block, you can use <code>this</code> to reference the element being
 * iterated over.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class EachHelper implements Helper<Object> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<Object> INSTANCE = new EachHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "each";

  @SuppressWarnings({"rawtypes", "unchecked" })
  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    if (context instanceof Iterable) {
      return iterableContext((Iterable) context, options);
    } else if (context != null) {
      return hashContext(context, options);
    }
    return options.buffer();
  }

  /**
   * Iterate over a hash like object.
   *
   * @param it The context object.
   * @param options The helper options.
   * @return The string output.
   * @throws IOException If something goes wrong.
   */
  private CharSequence hashContext(final Object it, final Options options)
      throws IOException {
    Iterator<Entry<String, Object>> loop = options.propertySet(it).iterator();
    Context parent = options.context;
    boolean first = true;
    Options.Buffer buffer = options.buffer();
    Template fn = options.fn;
    while (loop.hasNext()) {
      Entry<String, Object> entry = loop.next();
      String key = entry.getKey();
      Context itCtx = Context.newBuilder(parent, entry.getValue())
          .combine("@key", key)
          .combine("@first", first ? "first" : "")
          .combine("@last", !loop.hasNext() ? "last" : "")
          .build();
      buffer.append(options.apply(fn, itCtx, Arrays.asList(it, key)));
      first = false;
    }
    return buffer;
  }

  /**
   * Iterate over an iterable object.
   *
   * @param context The context object.
   * @param options The helper options.
   * @return The string output.
   * @throws IOException If something goes wrong.
   */
  private CharSequence iterableContext(final Iterable<Object> context, final Options options)
      throws IOException {
    Options.Buffer buffer = options.buffer();
    if (options.isFalsy(context)) {
      buffer.append(options.inverse());
    } else {
      Iterator<Object> loop = context.iterator();
      int base = options.hash("base", 0);
      int index = base;
      Context parent = options.context;
      Template fn = options.fn;
      while (loop.hasNext()) {
        Object it = loop.next();
        boolean even = index % 2 == 0;
        Context itCtx = Context.newBuilder(parent, it)
            .combine("@index", index)
            .combine("@first", index == base ? "first" : "")
            .combine("@last", !loop.hasNext() ? "last" : "")
            .combine("@odd", even ? "" : "odd")
            .combine("@even", even ? "even" : "")
            // 1-based index
            .combine("@index_1", index + 1)
            .build();
        buffer.append(options.apply(fn, itCtx, Arrays.asList(it, index)));
        itCtx.destroy();
        index += 1;
      }
    }
    return buffer;
  }

}
