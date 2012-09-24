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
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.github.jknack.handlebars.helper;

import static org.apache.commons.lang3.Validate.isTrue;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

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

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    if (context == null) {
      return StringUtils.EMPTY;
    }
    isTrue(context instanceof Iterable,
        "found: '%s', expected '%s'", context, Iterable.class.getName());
    StringBuilder buffer = new StringBuilder();
    @SuppressWarnings("unchecked")
    Iterable<Object> elements = (Iterable<Object>) context;
    if (options.isEmpty(elements)) {
      buffer.append(options.inverse());
    } else {
      Iterator<Object> iterator = elements.iterator();
      int index = 0;
      while (iterator.hasNext()) {
        buffer
            .append(options.fn(next(options.wrap(context), iterator, index++)));
      }
    }
    return buffer.toString();
  }

  /**
   * Retrieve the next element available.
   *
   * @param parent The parent context.
   * @param iterator The element iterator.
   * @param index The nth position of this element. Zero base.
   * @return The next element available.
   */
  protected Object next(final Context parent, final Iterator<Object> iterator,
      final int index) {
    return iterator.next();
  }
}
