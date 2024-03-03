/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Options.Buffer;

/**
 * Normally, Handlebars templates are evaluated against the context passed into the compiled method.
 *
 * <p>You can shift the context for a section of a template by using the built-in with block helper.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class WithHelper implements Helper<Object> {

  /** A singleton instance of this helper. */
  public static final Helper<Object> INSTANCE = new WithHelper();

  /** The helper's name. */
  public static final String NAME = "with";

  @Override
  public Object apply(final Object context, final Options options) throws IOException {
    Buffer buffer = options.buffer();
    if (options.isFalsy(context)) {
      buffer.append(options.inverse(context));
    } else {
      buffer.append(options.fn(context));
    }
    return buffer;
  }
}
