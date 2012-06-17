package com.github.edgarespina.handlerbars.internal;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

enum Empty {

  ANY {
    @Override
    public boolean apply(final Object value) {
      return true;
    }

    @Override
    public boolean isEmpty(final Object value) {
      return value == null;
    }
  },

  BOOLEAN {
    @Override
    public boolean apply(final Object value) {
      return value instanceof Boolean;
    }

    @Override
    public boolean isEmpty(final Object value) {
      return !((Boolean) value).booleanValue();
    }
  },

  ITERABLE {
    @Override
    public boolean apply(final Object value) {
      return value instanceof Iterable;
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public boolean isEmpty(final Object value) {
      if (value instanceof Collection) {
        return ((Collection) value).size() == 0;
      }
      Iterator<Object> it = ((Iterable<Object>) value).iterator();
      return !it.hasNext();
    }
  },

  ARRAY {
    @Override
    public boolean apply(final Object value) {
      return value != null && value.getClass().isArray();
    }

    @Override
    public boolean isEmpty(final Object value) {
      int size = Array.getLength(value);
      return size == 0;
    }
  },

  MAP {
    @Override
    public boolean apply(final Object value) {
      return value instanceof Map;
    }

    @Override
    public boolean isEmpty(final Object value) {
      @SuppressWarnings("unchecked")
      Map<Object, Object> map = (Map<Object, Object>) value;
      return map.size() == 0;
    }
  };

  protected abstract boolean apply(Object value);

  public abstract boolean isEmpty(Object value);

  public static Empty get(final Object candidate) {
    Set<Empty> values = EnumSet.allOf(Empty.class);
    values.remove(ANY);
    for (Empty value : values) {
      if (value.apply(candidate)) {
        return value;
      }
    }
    return ANY;
  }

  public static boolean empty(final Object value) {
    return Empty.get(value).isEmpty(value);
  }
}
