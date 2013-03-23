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
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.github.jknack.handlebars.springmvc;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

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
 * This helper have a strong dependency to local-thread-bound variable for
 * accessing to the current user locale.
 *
 * @author edgar.espina
 * @since 0.5.5
 * @see LocaleContextHolder#getLocale()
 */
public class MessageSourceHelper implements Helper<String> {

  /**
   * A message source. Required.
   */
  private MessageSource messageSource;

  /**
   * Creates a new {@link MessageSourceHelperTest}.
   *
   * @param messageSource The message source. Required.
   */
  public MessageSourceHelper(final MessageSource messageSource) {
    this.messageSource =
        notNull(messageSource, "A message source is required.");
  }

  @Override
  public CharSequence apply(final String code, final Options options)
      throws IOException {
    Object[] args = options.params;
    String defaultMessage = options.hash("default");
    return messageSource
        .getMessage(code, args, defaultMessage, currentLocale());
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
