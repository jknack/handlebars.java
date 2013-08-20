package com.github.jknack.handlebars.springmvc.locale;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

import com.github.jknack.handlebars.Options;

/**
 * This resolver have a strong dependency to local-thread-bound variable for
 * accessing to the current user locale.
 *
 * @see LocaleContextHolder#getLocale()
 */
public class LocaleContextHolderResolver implements LocaleResolver {

  @Override
  public Locale getCurrent(final Options options) {
    return LocaleContextHolder.getLocale();
  }

}
