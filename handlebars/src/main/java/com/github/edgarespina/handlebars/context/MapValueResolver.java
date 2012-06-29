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
