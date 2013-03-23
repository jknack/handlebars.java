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
package com.github.jknack.handlebars;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

/**
 * A Jackson 1.x helper.
 * <p>
 * Basic usage:
 * </p>
 *
 * <pre>
 *  Handlebars hbs = new Handlebars();
 *
 *  hbs.registerHelper("json", JacksonHelper.INSTANCE);
 *
 *  ...
 *
 *  {{json model}}
 * </pre>
 * <p>
 * View class usage:
 * </p>
 *
 * <pre>
 *  Handlebars hbs = new Handlebars();
 *
 *  hbs.registerHelper("json", JacksonHelper.INSTANCE);
 *
 *  ...
 *
 *  {{json model view="foo.MyView"}}
 *
 * </pre>
 *
 * <p>
 * View alias usage:
 * </p>
 *
 * <pre>
 *
 *  Handlebars hbs = new Handlebars();
 *
 *  hbs.registerHelper("json", JacksonHelper.INSTANCE
 *    .viewAlias("myView", foo.MyView.class));
 *
 *  ...
 *
 *  {{json model view="myView"}}
 *
 * </pre>
 *
 * @author edgar.espina
 * @since 0.4.0
 */
public class JacksonHelper implements Helper<Object> {

  /**
   * A singleton version of {@link JacksonHelper}.
   */
  public static final Helper<Object> INSTANCE = new JacksonHelper();

  /**
   * The JSON parser.
   */
  private final ObjectMapper mapper;

  /**
   * Class alias registry.
   */
  private final Map<String, Class<?>> alias = new HashMap<String, Class<?>>();

  /**
   * Creates a new {@link JacksonHelper}.
   *
   * @param objectMapper The object's mapper. Required.
   */
  public JacksonHelper(final ObjectMapper objectMapper) {
    mapper = notNull(objectMapper, "The object mapper is required.");
  }

  /**
   * Creates a new {@link JacksonHelper}.
   */
  private JacksonHelper() {
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
        Class<?> viewClass = alias.get(viewName);
        if (viewClass == null) {
          viewClass = getClass().getClassLoader().loadClass(viewName);
        }
        writer = mapper.writerWithView(viewClass);
      } catch (ClassNotFoundException ex) {
        throw new IllegalArgumentException(viewName, ex);
      }
    } else {
      writer = mapper.writer();
    }
    return new Handlebars.SafeString(writer.writeValueAsString(context));
  }

  /**
   * Add an alias for the given view class.
   *
   * @param alias The view alias. Required.
   * @param viewClass The view class. Required.
   * @return This helper.
   */
  public JacksonHelper viewAlias(final String alias, final Class<?> viewClass) {
    this.alias.put(notEmpty(alias, "A view alias is required."),
        notNull(viewClass, "A view class is required."));
    return this;
  }
}
