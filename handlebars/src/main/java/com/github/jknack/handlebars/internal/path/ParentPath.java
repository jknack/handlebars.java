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
 * Resolve parent references, like <code>../path</code>.
 *
 * @author edgar
 * @since 4.0.1
 */
public class ParentPath implements PathExpression {

  @Override
  public Object eval(
      final ValueResolver resolver, final Context context, final Object data, final Chain chain) {
    Context parent = context.parent();
    if (parent == null) {
      return null;
    }
    return parent.get(chain.path());
  }

  @Override
  public boolean local() {
    return true;
  }

  @Override
  public String toString() {
    return "../";
  }
}
