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

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Options.Buffer;

/**
 * <p>
 * Normally, Handlebars templates are evaluated against the context passed into
 * the compiled method.
 * </p>
 * <p>
 * You can shift the context for a section of a template by using the built-in
 * with block helper.
 * </p>
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class WithHelper implements Helper<Object> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<Object> INSTANCE = new WithHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "with";

  @Override
  public Object apply(final Object context, final Options options)
      throws IOException {
    Buffer buffer = options.buffer();
    if (options.isFalsy(context)) {
      buffer.append(options.inverse(context));
    } else {
      buffer.append(options.fn(context));
    }
    return buffer;
  }
}
