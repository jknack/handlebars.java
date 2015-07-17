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

import java.util.LinkedHashMap;
import java.util.Map;

/**
   * Better integration between java objects and js object. It check for data types at access time
   * and convert them if necessary.
   *
   * @author edgar
   */
@SuppressWarnings("serial")
class BetterNativeObject extends sun.org.mozilla.javascript.internal.NativeObject {

    /** Internal state. */
    private Map<Object, Object> state = new LinkedHashMap<Object, Object>();

    /**
     * Creates a new {@link BetterNativeObject}.
     *
     * @param map to be constructed from
     */
    public BetterNativeObject(final Map<?, Object> map) {
      for (Entry<?, Object> prop : map.entrySet()) {
        defineProperty(prop.getKey().toString(),
            prop.getValue(),
            sun.org.mozilla.javascript.internal.NativeObject.READONLY);
      }
    }

    /**
     * Override to translate and keep translated result, if necessary.
     * @param name of property
     * @param start see superclass javadoc
     * @return property value
     */
    @Override
    public Object get(final String name,
        final sun.org.mozilla.javascript.internal.Scriptable start) {
      Object value = state.get(name);
      if (value != null) {
        return value;
      }
      value = super.get(name, start);
      value = JavaObjectToJSTranslation.translateIfNecessary(value);
      state.put(name, value);
      return value;
    }

    @Override
    public Object getDefaultValue(final Class<?> arg0) {
      return super.toString();
    }
 }
