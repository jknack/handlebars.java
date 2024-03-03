/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Commons number function helpers.
 *
 * @author https://github.com/Jarlakxen
 */
public enum NumberHelper implements Helper<Object> {

  /**
   * You can use the isEven helper to return a value only if the first argument is even. Otherwise
   * return null.
   *
   * <pre>{@code
   * <li class="{{isEven value "leftBox"}}">
   * }</pre>
   *
   * If value is 2, the output will be "leftBox".
   */
  isEven {
    @Override
    public CharSequence safeApply(final Number value, final Options options) {
      return isEven(value) ? options.param(0, "even") : null;
    }
  },

  /**
   * You can use the isOdd helper to return a value only if the first argument is odd. Otherwise
   * return null.
   *
   * <pre>{@code
   * <li class="{{isOdd value "rightBox"}}">
   * }</pre>
   *
   * If value is 3, the output will be "rightBox".
   */
  isOdd {
    @Override
    public CharSequence safeApply(final Number value, final Options options) {
      return !isEven(value) ? options.param(0, "odd") : null;
    }
  },

  /**
   * You can use the stripes helper to return different value if the passed argument is odd or even.
   *
   * <pre>{@code
   * <tr class="{{stripes value "row-even" "row-odd"}}">
   * }</pre>
   *
   * If value is 2, the output will be "row-even".
   */
  stripes {
    @Override
    public CharSequence safeApply(final Number value, final Options options) {
      return isEven(value) ? options.param(0, "even") : options.param(1, "odd");
    }
  };

  @Override
  public Object apply(final Object context, final Options options) throws IOException {
    if (context instanceof Number) {
      return safeApply((Number) context, options);
    }
    return null;
  }

  /**
   * Apply the helper to the context.
   *
   * @param value The context object (param=0).
   * @param options The options object.
   * @return A string result.
   */
  protected abstract CharSequence safeApply(Number value, Options options);

  /**
   * Apply the helper to the context.
   *
   * @param value The number value.
   * @return true is even, false is odd
   */
  protected boolean isEven(final Number value) {
    return value.intValue() % 2 == 0;
  }

  /**
   * Register the helper in a handlebars instance.
   *
   * @param handlebars A handlebars object. Required.
   */
  public void registerHelper(final Handlebars handlebars) {
    notNull(handlebars, "The handlebars is required.");
    handlebars.registerHelper(this.name(), this);
  }

  /**
   * Register all the number helpers.
   *
   * @param handlebars The helper's owner. Required.
   */
  public static void register(final Handlebars handlebars) {
    notNull(handlebars, "A handlebars object is required.");
    NumberHelper[] helpers = values();
    for (NumberHelper helper : helpers) {
      helper.registerHelper(handlebars);
    }
  }
}
