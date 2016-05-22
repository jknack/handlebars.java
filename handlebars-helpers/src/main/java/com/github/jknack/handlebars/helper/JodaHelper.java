/**
 * Copyright (c) 2012-2015 Edgar Espina
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
package com.github.jknack.handlebars.helper;

import java.io.IOException;

import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Handlebars Helper for JodaTime ReadableInstance objects.
 *
 * @author @mrhanlon https://github.com/mrhanlon
 */
public enum JodaHelper implements Helper<ReadableInstant> {

  /**
   * A Helper for pattern-formatted <code>ReadableInstance</code>. Pattern usage
   * is as specified in {@link DateTimeFormat}. Defaults to
   * <code>M d y, H:m:s z</code>, for example
   * <code>11 15 1995, 14:32:10 CST</code>.
   */
  jodaPattern {

    @Override
    protected CharSequence safeApply(final ReadableInstant time, final Options options) {
      String pattern = options.param(0, "M d y, H:m:s z");
      return DateTimeFormat.forPattern(pattern).print(time);
    }

  },

  /**
   * A Helper for style-formatted <code>ReadableInstant</code>. Style usage is
   * as specified in {@link DateTimeFormat}. Defaults to <code>MM</code>,
   * for example
   */
  jodaStyle {

    @Override
    protected CharSequence safeApply(final ReadableInstant time, final Options options) {
      String style = options.param(0, "MM");
      return DateTimeFormat.forStyle(style).print(time);
    }

  },

  /**
   * A Helper for ISO-formatted <code>ReadableInstant</code>.
   * Usages is detailed in {@link ISODateTimeFormat}.
   */
  jodaISO {

    @Override
    protected CharSequence safeApply(final ReadableInstant time, final Options options) {
      boolean includeMillis = options.param(1, false);
      if (includeMillis) {
        return ISODateTimeFormat.dateTime().print(time);
      } else {
        return ISODateTimeFormat.dateTimeNoMillis().print(time);
      }
    }

  };

  @Override
  public Object apply(final ReadableInstant time, final Options options) throws IOException {
    return safeApply(time, options);
  }

  /**
   * Apply the helper to the context.
   *
   * @param time
   *          The JodaTime ReadableInstant.
   * @param options
   *          Any formatting options, such as the Pattern, Style, or ISO format.
   * @return The String result.
   */
  protected abstract CharSequence safeApply(final ReadableInstant time, final Options options);

}
