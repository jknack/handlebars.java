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

import com.github.jknack.handlebars.Context;

/**
 * Var sub-expr param.
 *
 * @author edgar
 * @since 4.0.3
 */
public class VarParam implements Param {

  /** Value. */
  public final Variable fn;

  /**
   * Creates a new {@link VarParam}.
   *
   * @param value Value.
   */
  public VarParam(final Variable value) {
    this.fn = value;
  }

  @Override
  public Object apply(final Context context) throws IOException {
    return this.fn.value(context, new FastStringWriter());
  }

  @Override
  public String toString() {
    return fn.text();
  }
}
