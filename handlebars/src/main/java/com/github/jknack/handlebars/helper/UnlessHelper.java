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
 * You can use the unless helper as the inverse of the if helper. Its block
 * will be rendered if the expression returns a falsy value.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class UnlessHelper implements Helper<Object> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<Object> INSTANCE = new UnlessHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "unless";

  @Override
  public Object apply(final Object context, final Options options)
      throws IOException {
    Buffer buffer = options.buffer();
    if (options.isFalsy(context)) {
      buffer.append(options.fn());
    } else {
      buffer.append(options.inverse());
    }
    return buffer;
  }
}
