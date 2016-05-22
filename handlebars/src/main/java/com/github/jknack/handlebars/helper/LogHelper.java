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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;

/**
 * Log a message. Usage:
 *
 * <pre>
 * {{log "Look at me!" }}
 *
 * {{log "This is logged" foo "And so is this"}}
 *
 * {{log "Log!" level="error"}}
 *
 * {{#log}}
 *  Hi {{name}}!
 * {{/log}}
 * </pre>
 *
 * @author edgar.espina
 * @since 4.0.1
 */
public class LogHelper implements Helper<Object> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<Object> INSTANCE = new LogHelper();

  /** The logging system. */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * The helper's name.
   */
  public static final String NAME = "log";

  @Override
  public Object apply(final Object context, final Options options)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    String level = options.hash("level", "info");
    TagType tagType = options.tagType;
    if (tagType.inline()) {
      sb.append(context);
      for (int i = 0; i < options.params.length; i++) {
        sb.append(" ").append(options.param(i));
      }
    } else {
      sb.append(options.fn());
    }
    switch (level) {
      case "error":
        log.error(sb.toString().trim());
        break;
      case "debug":
        log.debug(sb.toString().trim());
        break;
      case "warn":
        log.warn(sb.toString().trim());
        break;
      case "trace":
        log.trace(sb.toString().trim());
        break;
      default:
        log.info(sb.toString().trim());
    }
    return null;
  }
}
