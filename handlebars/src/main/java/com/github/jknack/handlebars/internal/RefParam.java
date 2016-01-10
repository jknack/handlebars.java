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

import java.util.List;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.PathExpression;

/**
 * Look param in the given context.
 *
 * @author edgar
 * @since 4.0.3
 */
public class RefParam implements Param {

  /** Compiled expression . */
  private final List<PathExpression> value;

  /**
   * Creates a new {@link RefParam}.
   *
   * @param value A compiled expression.
   */
  public RefParam(final List<PathExpression> value) {
    this.value = value;
  }

  @Override
  public Object apply(final Context context) {
    return context.get(this.value);
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
