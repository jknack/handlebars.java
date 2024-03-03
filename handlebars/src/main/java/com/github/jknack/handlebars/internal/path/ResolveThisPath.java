/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal.path;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.ValueResolver;

/**
 * Resolve a this/current path, like <code>.</code>,<code>this</code> or <code>./</code>.
 *
 * @author edgar
 * @since 4.0.1
 */
public class ResolveThisPath extends ThisPath {

  /**
   * Creates a new path resolver.
   *
   * @param name A this path.
   */
  public ResolveThisPath(final String name) {
    super(name);
  }

  @Override
  public Object eval(
      final ValueResolver resolver, final Context context, final Object data, final Chain chain) {
    Object value = resolver.resolve(data);
    return value == null ? data : value;
  }
}
