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
import java.util.Deque;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Decorator;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

/**
 * Inline partials via {@link Decorator} API.
 *
 * <pre>
 * {{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}
 * </pre>
 *
 * @author edgar
 * @since 4.0.0
 */
public class InlineDecorator implements Decorator {

  /**
   * A singleton instance of this helper.
   */
  public static final Decorator INSTANCE = new InlineDecorator();

  @Override
  public void apply(final Template fn, final Options options) throws IOException {
    Deque<Map<String, Template>> partials = options.data(Context.INLINE_PARTIALS);
    partials.getLast().put((String) options.param(0), options.fn);
  }

}
