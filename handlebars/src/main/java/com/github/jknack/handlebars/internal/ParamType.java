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
package com.github.jknack.handlebars.internal;

import com.github.jknack.handlebars.Context;

/**
 * A strategy for parameter type resolver.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
enum ParamType {
  /**
   * Resolve the parameter type as {@link Context#context()}.
   */
  CONTEXT {
    @Override
    boolean apply(final Object param) {
      return param instanceof Context;
    }

    @Override
    Object doParse(final Context scope, final Object param) {
      return ((Context) param).model();
    }
  },

  /**
   * Matches ".*" expressions.
   */
  STRING {
    @Override
    boolean apply(final Object param) {
      if (param instanceof String) {
        String string = (String) param;
        return string.startsWith("\"") && string.endsWith("\"")
            || string.startsWith("'") && string.endsWith("'");
      }
      return false;
    }

    @Override
    Object doParse(final Context scope, final Object param) {
      String string = (String) param;
      return string.subSequence(1, string.length() - 1);
    }
  },

  /**
   * Matches <code>true</code> or <code>false</code>.
   */
  BOOLEAN {
    @Override
    boolean apply(final Object param) {
      return param instanceof Boolean;
    }

    @Override
    Object doParse(final Context scope, final Object param) {
      return param;
    }
  },

  /**
   * Matches a integer value.
   */
  INTEGER {
    @Override
    boolean apply(final Object param) {
      return param instanceof Integer;
    }

    @Override
    Object doParse(final Context scope, final Object param) {
      return param;
    }
  },

  /**
   * Matches a reference value.
   */
  REFERENCE {
    @Override
    boolean apply(final Object param) {
      return param instanceof String;
    }

    @Override
    Object doParse(final Context scope, final Object param) {
      return scope.get((String) param);
    }
  };

  /**
   * True if the current strategy applies for the given value.
   *
   * @param param The candidate value.
   * @return True if the current strategy applies for the given value.
   */
  abstract boolean apply(Object param);

  /**
   * Parse the candidate param.
   *
   * @param context The context.
   * @param param The candidate param.
   * @return A parsed value.
   */
  abstract Object doParse(Context context, Object param);

  /**
   * Parse the given parameter to a runtime representation.
   *
   * @param context The current context.
   * @param param The candidate parameter.
   * @return The parameter value at runtime.
   */
  public static Object parse(final Context context, final Object param) {
    return get(param).doParse(context, param);
  }

  /**
   * Find a strategy.
   *
   * @param param The candidate param.
   * @return A param type.
   */
  private static ParamType get(final Object param) {
    for (ParamType type : values()) {
      if (type.apply(param)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unsupported param: " + param);
  }
}
