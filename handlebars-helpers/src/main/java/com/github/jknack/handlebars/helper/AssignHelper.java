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

/**
 * You can use the assign helper to create auxiliary variables. Example:
 *
 * <pre>
 *  {{#assign "benefitsTitle"}} benefits.{{type}}.title {{/assign}}
 *  &lt;span class="benefit-title"&gt; {{i18n benefitsTitle}} &lt;/span&gt;
 * </pre>
 *
 * @author https://github.com/Jarlakxen
 */
public class AssignHelper implements Helper<String> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<String> INSTANCE = new AssignHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "assign";

  @Override
  public Object apply(final String variableName, final Options options)
      throws IOException {
    CharSequence finalValue = options.apply(options.fn);
    options.context.data(variableName, finalValue.toString().trim());
    return null;
  }
}
