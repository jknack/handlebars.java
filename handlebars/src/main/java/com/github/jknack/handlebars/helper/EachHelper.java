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
  public Object apply(final Object context, final Options options)
      throws IOException {
    if (context instanceof Iterable) {
      Options.Buffer buffer = options.buffer();
      Iterator<Object> loop = ((Iterable) context).iterator();
      int base = options.hash("base", 0);
      int index = base;
      boolean even = index % 2 == 0;
      Context parent = options.context;
      Template fn = options.fn;
      while (loop.hasNext()) {
        Object it = loop.next();
        Context itCtx = Context.newContext(parent, it);
        itCtx.combine("@index", index)
            .combine("@first", index == base ? "first" : "")
            .combine("@last", !loop.hasNext() ? "last" : "")
            .combine("@odd", even ? "" : "odd")
            .combine("@even", even ? "even" : "")
            // 1-based index
            .combine("@index_1", index + 1);
        buffer.append(options.apply(fn, itCtx, Arrays.asList(it, index)));
        index += 1;
        even = !even;
      }
      // empty?
      if (base == index) {
        buffer.append(options.inverse());
      }
      return buffer;
    } else if (context != null) {
      Iterator loop = options.propertySet(context).iterator();
      Context parent = options.context;
      boolean first = true;
      Options.Buffer buffer = options.buffer();
      Template fn = options.fn;
      while (loop.hasNext()) {
        Entry entry = (Entry) loop.next();
        Object key = entry.getKey();
        Object value = entry.getValue();
        Context itCtx = Context.newBuilder(parent, value)
            .combine("@key", key)
            .combine("@first", first ? "first" : "")
            .combine("@last", !loop.hasNext() ? "last" : "")
            .build();
        buffer.append(options.apply(fn, itCtx, Arrays.asList(value, key)));
        first = false;
      }
      // empty?
      if (first) {
        buffer.append(options.inverse());
      }
      return buffer;
    }
    return options.buffer();
  }

}
