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
 * {{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}
 * </pre>
 *
 * @author edgar
 * @since 4.0.0
 */
public class InlineDecorator implements Decorator {

  /**
   * A singleton instance of this helper.
   */
  public static final Decorator INSTANCE = new InlineDecorator();

  @Override
  public void apply(final Template fn, final Options options) throws IOException {
    Deque<Map<String, Template>> partials = options.data(Context.INLINE_PARTIALS);
    partials.getLast().put((String) options.param(0), options.fn);
  }

}
