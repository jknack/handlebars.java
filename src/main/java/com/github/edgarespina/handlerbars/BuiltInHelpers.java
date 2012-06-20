package com.github.edgarespina.handlerbars;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
   * <p>
   * Usage:
   * </p>
   *
   * <pre>
   *    {{dateFormat date ["format"]}}
   * </pre>
   *
   * Format parameters is one of:
   * <ul>
   * <li>"full": full date format. For example: Tuesday, June 19, 2012</li>
   * <li>"long": long date format. For example: June 19, 2012</li>
   * <li>"medium": medium date format. For example: Jun 19, 2012</li>
   * <li>"short": short date format. For example: 6/19/12</li>
   * <li>"pattern": a date pattern.</li>
   * </ul>
   * Otherwise, the default formatter will be used.
   */
  DATE_FORMAT {
    /**
     * The default date styles.
     */
    @SuppressWarnings("serial")
    private Map<String, Integer> styles = new HashMap<String, Integer>()
    {
      {
        put("full", DateFormat.FULL);
        put("long", DateFormat.LONG);
        put("medium", DateFormat.MEDIUM);
        put("short", DateFormat.SHORT);
      }
    };

    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      if (context == null) {
        return null;
      }
      Date date = (Date) context;
      final DateFormat dateFormat;
      String pattern = null;
      if (options.params.length > 0) {
        pattern = options.param(0);
        Integer style = styles.get(pattern);
        if (style == null) {
          dateFormat = new SimpleDateFormat(pattern);
        } else {
          dateFormat = DateFormat.getDateInstance(style);
        }
      } else {
        dateFormat = DateFormat.getDateInstance();
      }
      return dateFormat.format(date);
    }

    @Override
    protected void add(final Handlebars handlebars) {
      add("dateFormat", handlebars);
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
   * Add this helper to the handle bar instance.
   *
   * @param handlebars The handlebars instance.
   */
  protected void add(final Handlebars handlebars) {
    add(name().toLowerCase(), handlebars);
  }

  /**
   * Add this helper to the handle bar instance.
   *
   * @param name The helper's name.
   * @param handlebars The handlebars instance.
   */
  protected void add(final String name, final Handlebars handlebars) {
    handlebars.registerHelper(name, this);
  }

  /**
   * Regiter all the built-in helpers.
   *
   * @param handlebars The helper's owner.
   */
  static void register(final Handlebars handlebars) {
    checkNotNull(handlebars, "A handlebars object is required.");
    BuiltInHelpers[] helpers = values();
    for (BuiltInHelpers helper : helpers) {
      helper.add(handlebars);
    }
  }
}
