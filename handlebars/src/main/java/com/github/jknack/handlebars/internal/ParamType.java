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
 * A strategy for parameter type resolver.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public enum ParamType {
  /**
   * Matches ".*" expressions.
   */
  STRING {
    @Override
    boolean apply(final Object param) {
      if (param instanceof String) {
        String string = (String) param;
        int len = string.length();
        return string.charAt(0) == '"' && string.charAt(len - 1) == '"'
            || string.charAt(0) == '\'' && string.charAt(len - 1) == '\'';
      }
      return false;
    }

    @Override
    Object doParse(final Context scope, final Object param) {
      String string = (String) param;
      return string.substring(1, string.length() - 1);
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
  },

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
   * Sub-expression are inner-helper invocations.
   */
  SUB_EXPRESSION {
    @Override
    boolean apply(final Object param) {
      return param instanceof Variable;
    }

    @Override
    Object doParse(final Context context, final Object param) throws IOException {
      Variable var = (Variable) param;
      return var.apply(context);
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
   * @throws IOException If param can't be applied.
   */
  abstract Object doParse(Context context, Object param) throws IOException;

  /**
   * Parse the given parameter to a runtime representation.
   *
   * @param context The current context.
   * @param param The candidate parameter.
   * @return The parameter value at runtime.
   * @throws IOException If param can't be applied.
   */
  public static Object parse(final Context context, final Object param) throws IOException {
    if (param instanceof String) {
      // string literal
      String str = (String) param;
      if (str.charAt(0) == '"') {
        return str.substring(1, str.length() - 1);
      } else if (str.charAt(0) == '\'') {
        return str.substring(1, str.length() - 1);
      }
      // reference
      return context.get(str);
    }
    // context ref
    if (param instanceof Context) {
      return ((Context) param).model();
    }
    // subexpression
    if (param instanceof Variable) {
      return ((Variable) param).apply(context);
    }
    // noop
    return param;
  }

}
