/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import static org.apache.commons.lang3.Validate.isTrue;

import java.io.IOException;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * The partial registry helper. It stores templates in the current execution context. Later the
 * {@link BlockHelper} helper read the registry and apply the template.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class PartialHelper implements Helper<Object> {

  /** A singleton instance of this helper. */
  public static final Helper<Object> INSTANCE = new PartialHelper();

  /** The helper's name. */
  public static final String NAME = "partial";

  @Override
  public Object apply(final Object context, final Options options) throws IOException {
    isTrue(context instanceof String, "found '%s', expected 'partial's name'", context);

    options.partial((String) context, options.fn);
    options.data(Context.PARTIALS + "#" + context + "#hash", options.hash);
    options.data(Context.PARTIALS + "#" + context + "#type", options.tagType);
    return null;
  }
}
