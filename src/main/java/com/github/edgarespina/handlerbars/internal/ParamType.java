package com.github.edgarespina.handlerbars.internal;

import java.util.EnumSet;


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
      return ((Context) param).target();
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
        return string.startsWith("\"") && string.endsWith("\"");
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
      return scope.get(param);
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
   * Parse the canidate param.
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
    EnumSet<ParamType> types = EnumSet.allOf(ParamType.class);
    for (ParamType type : types) {
      if (type.apply(param)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unsupported param: " + param);
  }
}
