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

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.TemplateLoader;
import com.github.jknack.handlebars.ValueResolver;

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
   * The value's resolvers.
   */
  private ValueResolver[] valueResolvers = ValueResolver.VALUE_RESOLVERS;

  /**
   * Fail on missing file. Default is: true.
   */
  private boolean failOnMissingFile = true;

  /**
   * The helper registry.
   */
  private final Map<String, Helper<?>> helpers =
      new HashMap<String, Helper<?>>();

  /**
   * Creates a new {@link HandlebarsViewResolver}.
   *
   * @param viewClass The view's class. Required.
   */
  public HandlebarsViewResolver(
      final Class<? extends HandlebarsView> viewClass) {
    setViewClass(viewClass);
    setContentType(DEFAULT_CONTENT_TYPE);
    setPrefix(TemplateLoader.DEFAULT_PREFIX);
    setSuffix(TemplateLoader.DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link HandlebarsViewResolver}.
   */
  public HandlebarsViewResolver() {
    this(HandlebarsView.class);
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
    try {
      view.setTemplate(handlebars.compile(URI.create(url)));
      view.setValueResolver(valueResolvers);
    } catch (FileNotFoundException ex) {
      if (failOnMissingFile) {
        throw ex;
      }
      logger.debug("File not found: " + url);
    }
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
    // Creates a new template loader.
    TemplateLoader templateLoader =
        createTemplateLoader(getApplicationContext());
    // Creates a new handlebars object.
    handlebars = notNull(createHandlebars(templateLoader),
        "A handlebars object is required.");
    // Copy all the helpers
    for (Entry<String, Helper<?>> entry : helpers.entrySet()) {
      handlebars.registerHelper(entry.getKey(), entry.getValue());
    }
    // clear the local helpers
    helpers.clear();

    // Add a message source helper
    handlebars.registerHelper("message", new MessageSourceHelper(
        getApplicationContext()));
  }

  /**
   * Creates a new {@link Handlebars} object using the
   * {@link SpringTemplateLoader}.
   *
   * @param templateLoader A template loader.
   * @return A new handlebar's object.
   */
  protected Handlebars createHandlebars(final TemplateLoader templateLoader) {
    return new Handlebars(templateLoader);
  }

  /**
   * Creates a new template loader.
   *
   * @param context The application's context.
   * @return A new template loader.
   */
  protected TemplateLoader createTemplateLoader(
      final ApplicationContext context) {
    TemplateLoader templateLoader = new SpringTemplateLoader(context);
    // Override prefix and suffix.
    templateLoader.setPrefix(getPrefix());
    templateLoader.setSuffix(getSuffix());
    return templateLoader;
  }

  /**
   * A handlebars instance.
   *
   * @return A handlebars instance.
   */
  public Handlebars getHandlebars() {
    if (handlebars == null) {
      throw new IllegalStateException(
          "afterPropertiesSet() method hasn't been call it.");
    }
    return handlebars;
  }

  /**
   * Set the value resolvers.
   *
   * @param valueResolvers The value resolvers. Required.
   */
  public void setValueResolvers(final ValueResolver... valueResolvers) {
    this.valueResolvers = notEmpty(valueResolvers,
        "At least one value-resolver must be present.");
  }

  /**
   * True, if the view resolver should fail on missing files. Default is: true.
   *
   * @param failOnMissingFile True, if the view resolver should fail on
   *        missing files. Default is: true.
   */
  public void setFailOnMissingFile(final boolean failOnMissingFile) {
    this.failOnMissingFile = failOnMissingFile;
  }

  /**
   * Register a Handlebars helper.
   *
   * @param name The helper's name. Required.
   * @param helper The helper instance. Required.
   * @return This view resolver.
   */
  public HandlebarsViewResolver registerHelper(final String name,
      final Helper<?> helper) {
    helpers.put(name, helper);
    return this;
  }

  /**
   * Set the helper registry.
   *
   * @param helpers The helper's registry. Required.
   */
  public void setHelpers(final Map<String, Helper<?>> helpers) {
    this.helpers.clear();
    this.helpers.putAll(helpers);
  }
}
