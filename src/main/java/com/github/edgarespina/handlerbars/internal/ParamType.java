package com.github.edgarespina.handlerbars.internal;

import java.util.EnumSet;

import com.github.edgarespina.handlerbars.Scope;

public enum ParamType {
  STRING {
    @Override
    public boolean apply(final Object param) {
      if (param instanceof String) {
        String string = (String) param;
        return string.startsWith("\"") && string.endsWith("\"");
      }
      return false;
    }

    @Override
    public Object parse(final Scope scope, final Object param) {
      String string = (String) param;
      return string.subSequence(1, string.length() - 1);
    }
  },

  BOOLEAN {
    @Override
    public boolean apply(final Object param) {
      return param instanceof Boolean;
    }

    @Override
    public Object parse(final Scope scope, final Object param) {
      return param;
    }
  },

  INTEGER {
    @Override
    public boolean apply(final Object param) {
      return param instanceof Integer;
    }

    @Override
    public Object parse(final Scope scope, final Object param) {
      return param;
    }
  },

  VALUE {
    @Override
    public boolean apply(final Object param) {
      return param instanceof String;
    }

    @Override
    public Object parse(final Scope scope, final Object param) {
      return scope.get(param);
    }
  };

  public abstract boolean apply(Object param);

  public abstract Object parse(Scope scope, Object param);

  public static ParamType get(final Object param) {
    EnumSet<ParamType> types = EnumSet.allOf(ParamType.class);
    for (ParamType type : types) {
      if (type.apply(param)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unsupported param: " + param);
  }
}