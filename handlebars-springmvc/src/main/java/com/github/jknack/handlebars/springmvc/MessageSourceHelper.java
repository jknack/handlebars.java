/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * A helper that delegates to a {@link MessageSource} instance. Usage:
 *
 * <pre>
 *  {{message "code" args* [default="default message"] }}
 * </pre>
 *
 * Where:
 *
 * <ul>
 *   <li>code: String literal. Required.
 *   <li>args: Object. Optional
 *   <li>default: A default message. Optional.
 * </ul>
 *
 * This helper have a strong dependency to local-thread-bound variable for accessing to the current
 * user locale.
 *
 * @author edgar.espina
 * @since 0.5.5
 * @see LocaleContextHolder#getLocale()
 */
public class MessageSourceHelper implements Helper<String> {

  /** A message source. Required. */
  private MessageSource messageSource;

  /**
   * Creates a new {@link MessageSourceHelper}.
   *
   * @param messageSource The message source. Required.
   */
  public MessageSourceHelper(final MessageSource messageSource) {
    this.messageSource = requireNonNull(messageSource, "A message source is required.");
  }

  @Override
  public Object apply(final String code, final Options options) throws IOException {
    Object[] args = options.params;
    String defaultMessage = options.hash("default");
    return messageSource.getMessage(code, args, defaultMessage, currentLocale());
  }

  /**
   * Resolve the current user locale.
   *
   * @return The current user locale.
   */
  protected Locale currentLocale() {
    return LocaleContextHolder.getLocale();
  }
}
