package com.github.edgarespina.handlerbars;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;

/**
 * Handlebars built-in helpers are present here.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public enum BuiltInHelpers implements Helper<Object> {

  /**
   * Just render the context.
   */
  NOOP {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      return options.fn();
    }
  },

  /**
   * <p>
   * Normally, Handlebars templates are evaluated against the context passed
   * into the compiled method.
   * </p>
   * <p>
   * You can shift the context for a section of a template by using the built-in
   * with block helper.
   * </p>
   */
  WITH {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      return options.fn(context);
    }
  },

  /**
   * You can iterate over a list using the built-in each helper. Inside the
   * block, you can use <code>this</code> to reference the element being
   * iterated over.
   */
  EACH {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      StringBuilder buffer = new StringBuilder();
      @SuppressWarnings("unchecked")
      Iterable<Object> elements = (Iterable<Object>) context;
      if (options.isEmpty(elements)) {
        buffer.append(options.inverse());
      } else {
        for (Object element : elements) {
          buffer.append(options.fn(element));
        }
      }
      return buffer.toString();
    }
  },

  /**
   * You can use the if helper to conditionally render a block. If its argument
   * returns false, null or empty list/array (a "falsy" value), Handlebars will
   * not render the block.
   */
  IF {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      if (options.isEmpty(context)) {
        return options.inverse();
      } else {
        return options.fn();
      }
    }
  },

  /**
   * You can use the unless helper as the inverse of the if helper. Its block
   * will be rendered if the expression returns a falsy value.
   */
  UNLESS {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      if (options.isEmpty(context)) {
        return options.fn();
      } else {
        return options.inverse();
      }
    }
  },

  /**
   * The block helper will replace its section with the partial of the
   * same name if it exists.
   */
  BLOCK {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      checkNotNull(context, "A template path is required.");

      String path = (String) context;
      Template template = options.partial(path);
      if (template == null) {
        template = options.handlebars.compile(URI.create(path));
        options.partial(path, template);
      }
      CharSequence result = options.apply(template);
      return result == null || result.length() == 0 ? options.fn() : result;
    }
  },

  /**
   * The partial registry helper. It stores templates in the current execution
   * context. Later the {@link #BLOCK} helper read the registry and apply the
   * template.
   */
  PARTIAL {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      options.partial((String) context, options.fn);
      return null;
    }
  },

  /**
   * The log helper.
   */
  LOG {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      Handlebars.log("Context %s", context);
      return null;
    }
  };

  /**
   * Regiter all the built-in helpers.
   *
   * @param handlebars The helper's owner.
   */
  static void register(final Handlebars handlebars) {
    checkNotNull(handlebars, "A handlebars object is required.");
    BuiltInHelpers[] helpers = values();
    for (BuiltInHelpers helper : helpers) {
      handlebars.registerHelper(helper.name().toLowerCase(), helper);
    }
  }
}
