/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.springmvc;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.TemplateLoader;

/**
 * A Handlebars {@link ViewResolver view resolver}.
 *
 * @author edgar.espina
 * @since 0.1
 */
public class HandlebarsViewResolver extends AbstractTemplateViewResolver
    implements InitializingBean {

  /**
   * The default content type.
   */
  public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";

  /**
   * The handlebars object.
   */
  private Handlebars handlebars;

  /**
   * Creates a new {@link HandlebarsViewResolver}.
   *
   * @param handlebars The handlebars object. Required.
   */
  public HandlebarsViewResolver(final Handlebars handlebars) {
    this();
    this.handlebars = notNull(handlebars, "A handlebars object is required.");
  }

  /**
   * Creates a new {@link HandlebarsViewResolver}.
   */
  public HandlebarsViewResolver() {
    setViewClass(HandlebarsView.class);
    setContentType(DEFAULT_CONTENT_TYPE);
    setPrefix(TemplateLoader.DEFAULT_PREFIX);
    setSuffix(TemplateLoader.DEFAULT_SUFFIX);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AbstractUrlBasedView buildView(final String viewName)
      throws Exception {
    return configure((HandlebarsView) super.buildView(viewName));
  }

  /**
   * Configure the handlebars view.
   *
   * @param view The handlebars view.
   * @return The configured view.
   * @throws IOException If a resource cannot be loaded.
   */
  protected AbstractUrlBasedView configure(final HandlebarsView view)
      throws IOException {
    String url = view.getUrl();
    // Remove prefix & suffix.
    url = url.substring(getPrefix().length(), url.length()
        - getSuffix().length());
    // Compile the template.
    view.setTemplate(handlebars.compile(URI.create(url)));
    return view;
  }

  /**
   * The required view class.
   *
   * @return The required view class.
   */
  @Override
  protected Class<?> requiredViewClass() {
    return HandlebarsView.class;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (handlebars == null) {
      // No handlebars instance was set, use default configuration.
      handlebars =
          new Handlebars(new SpringTemplateLoader(getApplicationContext()));
    }
    TemplateLoader templateLoader = handlebars.getTemplateLoader();
    // Override prefix and suffix.
    templateLoader.setPrefix(getPrefix());
    templateLoader.setSuffix(getSuffix());
  }

}
