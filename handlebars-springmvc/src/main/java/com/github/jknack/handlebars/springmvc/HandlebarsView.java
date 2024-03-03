/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

  /** The compiled template. */
  protected Template template;

  /** The value's resolvers. */
  protected ValueResolver[] valueResolvers;

  /** Merge model into the view. {@inheritDoc} */
  @Override
  protected void renderMergedTemplateModel(
      final Map<String, Object> model,
      final HttpServletRequest request,
      final HttpServletResponse response)
      throws IOException {
    Context context = Context.newBuilder(model).resolver(valueResolvers).build();
    try {
      template.apply(context, response.getWriter());
    } finally {
      context.destroy();
    }
  }

  /**
   * @return The underlying template for this view.
   */
  public Template getTemplate() {
    return template;
  }

  @Override
  public boolean checkResource(final Locale locale) {
    return template != null;
  }

  /**
   * Set the compiled template.
   *
   * @param template The compiled template. Required.
   */
  public void setTemplate(final Template template) {
    this.template = requireNonNull(template, "A handlebars template is required.");
  }

  /**
   * Set the value resolvers.
   *
   * @param valueResolvers The value resolvers. Required.
   */
  public void setValueResolver(final ValueResolver... valueResolvers) {
    this.valueResolvers =
        requireNonNull(valueResolvers, "At least one value-resolver must be present.");
  }

  @Override
  protected boolean isContextRequired() {
    return false;
  }
}
