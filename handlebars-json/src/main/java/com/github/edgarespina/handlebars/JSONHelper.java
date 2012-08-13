/**
 * Copyright (c) 2012 Edgar Espina
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
package com.github.edgarespina.handlebars;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Format the context object as JSON.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class JSONHelper implements Helper<Object> {

  /**
   * A singleton version of {@link JSONHelper}.
   */
  public static final Helper<Object> INSTANCE = new JSONHelper();

  /**
   * The JSON parser.
   */
  private final ObjectMapper mapper;

  /**
   * Creates a new {@link JSONHelper}.
   *
   * @param objectMapper The object's mapper. Required.
   */
  public JSONHelper(final ObjectMapper objectMapper) {
    this.mapper = checkNotNull(objectMapper, "The object mapper is required.");
  }

  /**
   * Creates a new {@link JSONHelper}.
   */
  private JSONHelper() {
    this(new ObjectMapper());
  }

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    if (context == null) {
      return "";
    }
    String viewName = options.hash("view", "");
    final ObjectWriter writer;
    if (viewName.length() > 0) {
      try {
        Class<?> viewClass = Class.forName(viewName);
        writer = mapper.writerWithView(viewClass);
      } catch (ClassNotFoundException ex) {
        throw new IllegalArgumentException(viewName, ex);
      }
    } else {
      writer = mapper.writer();
    }
    return new Handlebars.SafeString(writer.writeValueAsString(context));
  }

}
