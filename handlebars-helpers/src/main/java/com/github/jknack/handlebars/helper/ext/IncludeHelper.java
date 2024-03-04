/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper.ext;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

/**
 * Allows to include partials with custom context. This is a port of
 * https://github.com/wycats/handlebars.js/pull/368
 *
 * @author https://github.com/c089
 */
public class IncludeHelper implements Helper<String> {
  /** A singleton instance of this helper. */
  public static final Helper<String> INSTANCE = new IncludeHelper();

  /** The helper's name. */
  public static final String NAME = "include";

  @Override
  public Object apply(final String partial, final Options options) throws IOException {
    // merge all the hashes into the context
    options.context.data(options.hash);
    Template template = options.handlebars.compile(partial);
    return new Handlebars.SafeString(template.apply(options.context));
  }
}
