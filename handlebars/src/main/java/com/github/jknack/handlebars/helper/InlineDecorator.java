/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Decorator;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

/**
 * Inline partials via {@link Decorator} API.
 *
 * <pre>
 * {{#*inline \"myPartial\"}}success{{/inline}}{{&gt; myPartial}}
 * </pre>
 *
 * @author edgar
 * @since 4.0.0
 */
public class InlineDecorator implements Decorator {

  /** A singleton instance of this helper. */
  public static final Decorator INSTANCE = new InlineDecorator();

  @Override
  public void apply(final Template fn, final Options options) throws IOException {
    Integer size = options.data(Context.PARAM_SIZE);
    if (size == null || size != 1) {
      throw new IllegalArgumentException("#*inline requires exactly one argument");
    }
    Object name = options.param(0);
    if (!(name instanceof String)) {
      throw new IllegalArgumentException("#*inline's argument must be a string literal");
    }
    Deque<Map<String, Template>> partials = options.data(Context.INLINE_PARTIALS);
    partials.getLast().put((String) name, options.fn);
  }
}
