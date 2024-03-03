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
 * Resolve a parent path, like <code>..</code>
 *
 * @author edgar
 * @since 4.0.1
 */
public class ResolveParentPath implements PathExpression {

  @Override
  public Object eval(
      final ValueResolver resolver, final Context context, final Object data, final Chain chain) {
    Context parent = context.parent();
    if (parent == null) {
      return null;
    }
    Object value = resolver.resolve(parent.model());
    return value == null ? data : value;
  }

  @Override
  public boolean local() {
    return true;
  }

  @Override
  public String toString() {
    return "..";
  }
}
