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

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractTemplateView;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.ValueResolver;

/**
 * A handlebars view implementation.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class HandlebarsView extends AbstractTemplateView {

  /**
   * The compiled template.
   */
  private Template template;

  /**
   * The value's resolvers.
   */
  private ValueResolver[] valueResolvers;

  /**
   * Merge model into the view. {@inheritDoc}
   */
  @Override
  protected void renderMergedTemplateModel(final Map<String, Object> model,
      final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {
    Context context = Context.newBuilder(model)
        .resolver(valueResolvers)
        .build();
    try {
      template.apply(context, response.getWriter());
    } finally {
      context.destroy();
    }
  }

  @Override
  public boolean checkResource(final Locale locale) throws Exception {
    return template != null;
  }

  /**
   * Set the compiled template.
   *
   * @param template The compiled template. Required.
   */
  void setTemplate(final Template template) {
    this.template = notNull(template, "A handlebars template is required.");
  }

  /**
   * Set the value resolvers.
   *
   * @param valueResolvers The value resolvers. Required.
   */
  void setValueResolver(final ValueResolver... valueResolvers) {
    this.valueResolvers = notEmpty(valueResolvers,
        "At least one value-resolver must be present.");
  }
}
