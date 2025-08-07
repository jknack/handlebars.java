/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal.path;

import java.util.List;
import java.util.Set;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.PathExpression;
import com.github.jknack.handlebars.ValueResolver;

/**
 * Resolve <code>@data</code> expression by lookup whole/complete name first and then without the at
 * symbol.
 *
 * @author edgar
 * @since 4.0.1
 */
public class DataPath implements PathExpression {

  /** First and last are special in 21. */
  private static final Set<String> FIXED = Set.of("@last", "@first");

  /** Property name. */
  private String name;

  /** Property name without @. */
  private String nameWithoutAtSymbol;

  /**
   * Creates a new {@link DataPath} expression.
   *
   * @param name Expression name.
   */
  public DataPath(final String name) {
    this.name = name;
    this.nameWithoutAtSymbol = name.substring(1);
  }

  @Override
  public Object eval(
      final ValueResolver resolver, final Context context, final Object data, final Chain chain) {
    // with @
    Object value = resolver.resolve(data, name);
    if (value == null) {
      if (data instanceof List) {
        if (!FIXED.contains(name)) {
          // without @
          value = resolver.resolve(data, nameWithoutAtSymbol);
        }
      } else {
        // without @
        value = resolver.resolve(data, nameWithoutAtSymbol);
      }
    }
    return chain.next(resolver, context, value);
  }

  @Override
  public boolean local() {
    return false;
  }

  @Override
  public String toString() {
    return name;
  }
}
