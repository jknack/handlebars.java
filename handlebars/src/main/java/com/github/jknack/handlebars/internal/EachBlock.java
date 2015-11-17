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
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.EachHelper;

/**
 * A specialized version of {@link EachHelper}. This block is applied when users explicitly use the
 * <code>each</code> helper in their code.
 *
 * Please note, existing helper {@link EachHelper} still apply for Mustache like templates where the
 * helper is assigned at runtime base on data type.
 *
 * @author edgar
 * @since 4.0.2
 */
public class EachBlock extends Block {

  /**
   * Creates a new {@link EachBlock}.
   *
   * @param handlebars The handlebars object.
   * @param name The section's name.
   * @param inverted True if it's inverted.
   * @param type Block type: <code>#</code>, <code>^</code>, <code>#*</code>, <code>{{</code>.
   * @param params The parameter list.
   * @param hash The hash.
   * @param blockParams The block param names.
   */
  public EachBlock(final Handlebars handlebars, final String name, final boolean inverted,
      final String type, final List<Object> params, final Map<String, Object> hash,
      final List<String> blockParams) {
    super(handlebars, name, inverted, type, params, hash, blockParams);
  }

  @Override
  protected void postInit() {
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void merge(final Context context, final Writer writer) throws IOException {
    Object it = Transformer.transform(determineContext(context));
    int size = 0;
    if (it instanceof Iterable) {
      size = iterable((Iterable<Object>) it, context, writer);
    } else if (it != null) {
      size = hash(it, context, writer);
    }
    if (size == 0) {
      inverse.apply(context, writer);
    }
  }

  /**
   * Iterate over a hash like object.
   *
   * @param context The context object.
   * @param parent Parent context.
   * @param writer A writer.
   * @return The number of iteration applied.
   * @throws IOException If something goes wrong.
   */
  private int hash(final Object context, final Context parent, final Writer writer)
      throws IOException {
    Iterator<Entry<String, Object>> loop = parent.propertySet(context).iterator();
    boolean first = true;
    boolean useBlockContext = this.blockParams.size() > 0;
    while (loop.hasNext()) {
      Entry<String, Object> entry = loop.next();
      String key = entry.getKey();
      Object it = entry.getValue();
      Context itCtx = useBlockContext
          ? Context.newBlockParamContext(parent, blockParams, Arrays.asList(context, key))
          : Context.newContext(parent, it);
      itCtx.combine("@key", key)
          .combine("@first", first ? "first" : "")
          .combine("@last", !loop.hasNext() ? "last" : "");
      body.apply(itCtx, writer);
      first = false;
    }
    return 1;
  }

  /**
   * Iterate over an iterable object.
   *
   * @param context The context object.
   * @param parent Parent context.
   * @param writer A writer.
   * @return The number of iteration applied.
   * @throws IOException If something goes wrong.
   */
  private int iterable(final Iterable<Object> context, final Context parent, final Writer writer)
      throws IOException {
    Map<String, Object> hash = hash(parent);
    Integer base = (Integer) hash.get("base");
    if (base == null) {
      base = 0;
    }

    boolean useBlockContext = this.blockParams.size() > 0;
    int index = base;
    Iterator<Object> loop = context.iterator();
    while (loop.hasNext()) {
      Object it = loop.next();
      boolean even = index % 2 == 0;
      Context itCtx = useBlockContext
          ? Context.newBlockParamContext(parent, blockParams, Arrays.asList(it, index))
          : Context.newContext(parent, it);

      itCtx.combine("@index", index)
          .combine("@first", index == base ? "first" : "")
          .combine("@last", !loop.hasNext() ? "last" : "")
          .combine("@odd", even ? "" : "odd")
          .combine("@even", even ? "even" : "")
          // 1-based index
          .combine("@index_1", index + 1);

      body.apply(itCtx, writer);
      // buffer.append(options.apply(fn, itCtx, Arrays.asList(it, index)));
      itCtx.destroy();
      index += 1;
    }
    return index;
  }

}
