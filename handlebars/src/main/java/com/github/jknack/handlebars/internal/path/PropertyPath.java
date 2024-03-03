/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal.path;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.PathExpression;
import com.github.jknack.handlebars.ValueResolver;

/**
 * Resolve property paths.
 *
 * @author edgar
 * @since 4.0.1
 */
public class PropertyPath implements PathExpression {

  /** Path. */
  private String name;

  /** True, if we want to restrict lookup to current scope. */
  private boolean local;

  /**
   * A new {@link PropertyPath}.
   *
   * @param name A property path.
   * @param local True, if we want to restrict lookup to current scope.
   */
  public PropertyPath(final String name, final boolean local) {
    this.name = name;
    this.local = local;
  }

  @Override
  public Object eval(
      final ValueResolver resolver, final Context ctx, final Object data, final Chain chain) {
    return chain.next(resolver, ctx, resolver.resolve(data, name));
  }

  @Override
  public boolean local() {
    return local;
  }

  @Override
  public String toString() {
    return name;
  }
}
