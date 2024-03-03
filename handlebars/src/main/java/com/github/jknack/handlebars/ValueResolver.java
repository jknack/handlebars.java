/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;

/**
 * A hook interface for resolving values from the {@link Context context stack}.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public interface ValueResolver {

  /** A mark object. */
  Object UNRESOLVED = new Object();

  /**
   * Resolve the attribute's name in the context object. If a {@link #UNRESOLVED} is returned, the
   * {@link Context context stack} will continue with the next value resolver in the chain.
   *
   * @param context The context object. Not null.
   * @param name The attribute's name. Not null.
   * @return A {@link #UNRESOLVED} is returned, the {@link Context context stack} will continue with
   *     the next value resolver in the chain. Otherwise, it returns the associated value.
   */
  Object resolve(Object context, String name);

  /**
   * Resolve the the context object by optionally converting the value if necessary. If a {@link
   * #UNRESOLVED} is returned, the {@link Context context stack} will continue with the next value
   * resolver in the chain.
   *
   * @param context The context object. Not null.
   * @return A {@link #UNRESOLVED} is returned, the {@link Context context stack} will continue with
   *     the next value resolver in the chain. Otherwise, it returns the associated value.
   */
  Object resolve(Object context);

  /**
   * List all the properties and their values for the given object.
   *
   * @param context The context object. Not null.
   * @return All the properties and their values for the given object.
   */
  Set<Entry<String, Object>> propertySet(Object context);

  /**
   * Default value resolvers. Including:
   *
   * <p>- {@link MapValueResolver} - {@link JavaBeanValueResolver} - {@link MethodValueResolver}. On
   * Java 14 or higher.
   *
   * @return Immutable list of value resolvers.
   */
  static List<ValueResolver> defaultValueResolvers() {
    if (Handlebars.Utils.javaVersion14) {
      return unmodifiableList(
          asList(
              MapValueResolver.INSTANCE,
              JavaBeanValueResolver.INSTANCE,
              MethodValueResolver.INSTANCE));
    }
    return unmodifiableList(asList(MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE));
  }
}
