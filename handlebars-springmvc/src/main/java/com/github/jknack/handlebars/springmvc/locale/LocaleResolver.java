package com.github.jknack.handlebars.springmvc.locale;

import java.util.Locale;

import com.github.jknack.handlebars.Options;

/**
 * This is a generic interface for implementing
 * a locale resolver.
 */
public interface LocaleResolver {

  /**
   * Retrive the curren locale.
   *
   * @param options
   * @return get the locale
   */
  Locale getCurrent(final Options options);

}
