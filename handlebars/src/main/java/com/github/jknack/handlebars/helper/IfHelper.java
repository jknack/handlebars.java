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
 * You can use the if helper to conditionally render a block. If its argument returns false, null or
 * empty list/array (a "falsy" value), Handlebars will not render the block.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class IfHelper implements Helper<Object> {

  /** A singleton instance of this helper. */
  public static final Helper<Object> INSTANCE = new IfHelper();

  /** The helper's name. */
  public static final String NAME = "if";

  @Override
  public Object apply(final Object context, final Options options) throws IOException {
    Buffer buffer = options.buffer();
    if (options.isFalsy(context)) {
      buffer.append(options.inverse());
    } else {
      buffer.append(options.fn());
    }
    return buffer;
  }
}
