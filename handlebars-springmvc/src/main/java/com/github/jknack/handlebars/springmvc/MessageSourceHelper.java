/**
 * Copyright (c) 2012-2013 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.springmvc;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;

import org.springframework.context.MessageSource;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.springmvc.locale.LocaleResolver;

/**
 * <p>
 * A helper that delegates to a {@link MessageSource} instance.
 * </p>
 * Usage:
 *
 * <pre>
 *  {{message "code" args* [default="default message"] }}
 * </pre>
 *
 * Where:
 * <ul>
 * <li>code: String literal. Required.</li>
 * <li>args: Object. Optional</li>
 * <li>default: A default message. Optional.</li>
 * </ul>
 *
 * @author edgar.espina
 * @since 0.5.5
 */
public class MessageSourceHelper implements Helper<String> {

  /**
   * A message source. Required.
   */
  private MessageSource messageSource;

  /**
   * the locale resolver. Required.
   */
  private LocaleResolver localeResolver;

  /**
   * Creates a new {@link MessageSourceHelperTest}.
   *
   * @param messageSource
   *          The message source. Required.
   * @param localeResolver
   *          The locale resolver. Required.
   */
  public MessageSourceHelper(final MessageSource messageSource,
      final LocaleResolver localeResolver) {
    this.messageSource = notNull(messageSource, "A message source is required.");
    this.localeResolver = notNull(localeResolver,
        "A locale resolver is required.");
  }

  @Override
  public CharSequence apply(final String code, final Options options)
      throws IOException {
    Object[] args = options.params;
    String defaultMessage = options.hash("default");
    return messageSource.getMessage(code, args, defaultMessage,
        localeResolver.getCurrent(options));
  }
}
