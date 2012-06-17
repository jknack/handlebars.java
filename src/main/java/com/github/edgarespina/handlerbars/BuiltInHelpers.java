package com.github.edgarespina.handlerbars;

import java.io.IOException;

public enum BuiltInHelpers implements Helper<Object> {

  NOOP {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      return options.fn();
    }
  },

  WITH {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      return options.fn(context);
    }
  },

  EACH {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      StringBuilder buffer = new StringBuilder();
      @SuppressWarnings("unchecked")
      Iterable<Object> elements = (Iterable<Object>) context;
      if (options.empty(elements)) {
        buffer.append(options.inverse());
      } else {
        for (Object element : elements) {
          buffer.append(options.fn(element));
        }
      }
      return buffer.toString();
    }
  },

  IF {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      if (options.empty(context)) {
        return options.inverse();
      } else {
        return options.fn();
      }
    }
  },

  UNLESS {
    @Override
    public CharSequence apply(final Object context, final Options options)
        throws IOException {
      if (options.empty(context)) {
        return options.fn();
      } else {
        return options.inverse();
      }
    }
  };

  public static HelperRegistry registry() {
    return new HelperRegistry() {
      @Override
      public void register(final Handlebars handlebars) {
        BuiltInHelpers[] helpers = values();
        for (BuiltInHelpers helper : helpers) {
          handlebars.registerHelper(helper.name().toLowerCase(), helper);
        }
      }
    };
  }
}
