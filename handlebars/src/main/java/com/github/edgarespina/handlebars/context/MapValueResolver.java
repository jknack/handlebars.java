/**
 * Copyright (c) 2012 Edgar Espina
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
package com.github.edgarespina.handlebars.context;

import java.util.Map;

import com.github.edgarespina.handlebars.ValueResolver;

/**
 * A {@link Map} value resolver.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public enum MapValueResolver implements ValueResolver {

  /**
   * A singleton instance.
   */
  INSTANCE;

  @SuppressWarnings("rawtypes")
  @Override
  public Object resolve(final Object context, final String name) {
    if (context instanceof Map) {
      return ((Map) context).get(name);
    }
    return UNRESOLVED;
  }

}
