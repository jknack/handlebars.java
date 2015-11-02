package com.github.jknack.handlebars.helper;

import java.io.IOException;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Inline partials are implemented via helpers (not decorators like in handlebars.js). Decorators
 * are not supported in handlebars.java.
 *
 * <pre>
 * {{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}
 * </pre>
 *
 * @author edgar
 * @since 4.0.0
 */
public class InlineHelper implements Helper<String> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<String> INSTANCE = new InlineHelper();

  @Override
  public CharSequence apply(final String path, final Options options) throws IOException {
    Map<String, Object> partials = options.data(Context.INLINE_PARTIALS);
    partials.put(path, options.fn);
    return null;
  }

}
