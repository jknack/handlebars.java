/**
 * Copyright (c) 2012-2013 Edgar Espina
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
package com.github.jknack.handlebars.internal.js;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Better integration between java collections/arrays and js arrays. It check for data types
 * at access time and convert them when necessary.
 *
 * @author edgar
 */
@SuppressWarnings("serial")
class BetterNativeArray extends sun.org.mozilla.javascript.internal.NativeArray {

  /** Internal state of array. */
  private Map<Object, Object> state = new LinkedHashMap<Object, Object>();

  /**
   * A JS array.
   *
   * @param array Array.
   */
  public BetterNativeArray(final Object[] array) {
    super(array);
  }

  /**
   * A JS collection.
   *
   * @param collection collection.
   */
  public BetterNativeArray(final Collection<Object> collection) {
    this(collection.toArray(new Object[collection.size()]));
  }

  @Override
  public Object get(final int index, final sun.org.mozilla.javascript.internal.Scriptable start) {
    Object value = state.get(index);
    if (value != null) {
      return value;
    }
    value = super.get(index, start);
    value = JavaObjectToJSTranslation.translateIfNecessary(value);
    state.put(index, value);
    return value;
  }

  @Override
  public Object getDefaultValue(final Class<?> arg0) {
    return super.toString();
  }
}
