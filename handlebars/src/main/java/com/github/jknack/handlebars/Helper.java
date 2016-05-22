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
package com.github.jknack.handlebars;

import java.io.IOException;

/**
 * Handlebars helpers can be accessed from any context in a template. You can
 * register a helper with the {@link Handlebars#registerHelper(String, Helper)}
 * method.
 *
 * @author edgar.espina
 * @param <T> The context object.
 * @since 0.1.0
 */
public interface Helper<T> {

  /**
   * Apply the helper to the context.
   *
   * @param context The context object.
   * @param options The options object.
   * @return A string result.
   * @throws IOException If a template cannot be loaded.
   */
  Object apply(T context, Options options) throws IOException;
}
