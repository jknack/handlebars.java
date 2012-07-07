/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.edgarespina.handlebars;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Iterator;

/**
 * Handlebars extended built-in helpers with pseudo vars are present here.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
enum PseudoVarsHelpers implements Helper<Object> {

  /**
   * You can iterate over a list using the built-in each helper. Inside the
   * block, you can use <code>this</code> to reference the element being
   * iterated over.
   */
  EACH {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      StringBuilder buffer = new StringBuilder();
      @SuppressWarnings("unchecked")
      Iterable<Object> elements = (Iterable<Object>) context;
      if (options.isEmpty(elements)) {
        buffer.append(options.inverse());
      } else {
        Iterator<Object> iterator = elements.iterator();
        int index = 0;
        while (iterator.hasNext()) {
          Object e = iterator.next();
          boolean first = index == 0;
          boolean last = !iterator.hasNext();
          Object element = Context.newBuilder(e)
              .combine("@index", index)
              .combine("@first", first ? "first" : "")
              .combine("@last", last ? "last" : "")
              .build();
          buffer.append(options.fn(element));
          index++;
        }
      }
      return buffer.toString();
    }
  };

  /**
   * Add this helper to the handle bar instance.
   *
   * @param handlebars The handlebars instance.
   */
  protected void add(final Handlebars handlebars) {
    add(name().toLowerCase(), handlebars);
  }

  /**
   * Add this helper to the handle bar instance.
   *
   * @param name The helper's name.
   * @param handlebars The handlebars instance.
   */
  protected void add(final String name, final Handlebars handlebars) {
    handlebars.registerHelper(name, this);
  }

  /**
   * Regiter all the built-in helpers.
   *
   * @param handlebars The helper's owner.
   */
  static void register(final Handlebars handlebars) {
    checkNotNull(handlebars, "A handlebars object is required.");
    PseudoVarsHelpers[] helpers = values();
    for (PseudoVarsHelpers helper : helpers) {
      helper.add(handlebars);
    }
  }
}
