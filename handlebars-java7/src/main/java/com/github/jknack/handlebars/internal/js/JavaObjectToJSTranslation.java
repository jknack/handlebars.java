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

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

/**
 * Better integration between java collections/arrays and js arrays. It check for data types
 * at access time and convert them when necessary.
 *
 * @author edgar
 */
@SuppressWarnings("serial")
final class JavaObjectToJSTranslation {

  /**
   * The logging system.
   */
  private static final Logger logger = getLogger(JavaObjectToJSTranslation.class);


  /**
   * private constructor because checkstyle asks for it.
   */
  private JavaObjectToJSTranslation() {
  }

  /**
   * Translate Map, Collection and Array.
   * @param object to be translated
   * @return the translated object, or same
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static Object translate(final Object object) {
      if (Map.class.isInstance(object)) {
          return new BetterNativeObject((Map) object);
      } else if (Collection.class.isInstance(object)) {
          return new BetterNativeArray((Collection) object);
      } else if (object.getClass().isArray()) {
          return new BetterNativeArray((Object[]) object);
      }
      throw new RuntimeException(
          "Handlebars internal error: cannot translate object, "
          + "check whether translate() and needsTranslation() correspond!");
  }

  /**
   * Whether given object needs to be translated.
   * @param object in question
   * @return whether needs translation
   */
  private static boolean needsTranslation(final Object object) {
    return object != null
        && (Map.class.isInstance(object)
            || Collection.class.isInstance(object)
            || object.getClass().isArray());
  }

  /**
   * Translate object if necessary, or return same.
   * @param object in question
   * @return translated or same
   */
  public static Object translateIfNecessary(final Object object) {
    if (needsTranslation(object)) {
      return translate(object);
    } else {
      return object;
    }
  }
}
