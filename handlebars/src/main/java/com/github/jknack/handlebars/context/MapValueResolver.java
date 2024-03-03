/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.context;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.jknack.handlebars.ValueResolver;

/**
 * A {@link Map} value resolver.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public enum MapValueResolver implements ValueResolver {

  /** A singleton instance. */
  INSTANCE;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Object resolve(final Object context, final String name) {
    Object value = null;
    if (context instanceof Map) {
      value = ((Map) context).get(name);
      // fallback to EnumMap
      if (value == null && context instanceof EnumMap) {
        EnumMap emap = (EnumMap) context;
        if (emap.size() > 0) {
          Enum first = (Enum) emap.keySet().iterator().next();
          Enum key = Enum.valueOf(first.getClass(), name);
          value = emap.get(key);
        }
      }
    }
    return value == null ? UNRESOLVED : value;
  }

  @Override
  public Object resolve(final Object context) {
    if (context instanceof Map) {
      return context;
    }
    return UNRESOLVED;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Set<Entry<String, Object>> propertySet(final Object context) {
    if (context instanceof Map) {
      return ((Map) context).entrySet();
    }
    return Collections.emptySet();
  }
}
