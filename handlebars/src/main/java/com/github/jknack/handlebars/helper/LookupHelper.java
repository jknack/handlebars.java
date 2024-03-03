/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import java.io.IOException;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Lookup helper, which allows to get a context variable.
 *
 * <p>It was introduced with dynamic partials:
 *
 * <pre>
 * {{&gt; (lookup '.' 'myVariable') }}
 * </pre>
 *
 * @author edgar
 * @since 2.2.0
 */
public class LookupHelper implements Helper<Object> {

  /** A singleton instance of this helper. */
  public static final Helper<Object> INSTANCE = new LookupHelper();

  /** The helper's name. */
  public static final String NAME = "lookup";

  @Override
  public Object apply(final Object context, final Options options) throws IOException {
    if (context == null) {
      return null;
    }
    if (options.params.length <= 0) {
      return context;
    }
    Context ctx = Context.newBuilder(options.context, context).build();
    Object lookup = ctx.get(options.param(0).toString());
    if (lookup == null) {
      return context;
    }
    return lookup;
  }
}
