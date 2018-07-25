/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.springmvc.webflux;

import static com.github.jknack.handlebars.springmvc.SpringUtils.createI18nSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;

import com.github.jknack.handlebars.Decorator;
import com.github.jknack.handlebars.Formatter;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache;
import com.github.jknack.handlebars.cache.NullTemplateCache;
import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.helper.DefaultHelperRegistry;
import com.github.jknack.handlebars.helper.I18nHelper;
import com.github.jknack.handlebars.helper.I18nSource;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.URLTemplateLoader;
import com.github.jknack.handlebars.springmvc.MessageSourceHelper;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;

/**
 * @author OhChang Kwon(ohchang.kwon@navercorp.com)
 */
public class ReactiveHandlebarsViewResolver extends UrlBasedViewResolver
    implements InitializingBean, HelperRegistry {

  /** The slf4j logger. */
  private static final Logger logger = LoggerFactory
      .getLogger(ReactiveHandlebarsViewResolver.class);

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
  private HelperRegistry registry = new DefaultHelperRegistry();

  /** True, if the message helper (based on {@link MessageSource}) should be registered. */
  private boolean registerMessageHelper = true;

  /**
   * If true, the i18n helpers will use a {@link MessageSource} instead of a plain
   * {@link ResourceBundle} .
   */
  private boolean bindI18nToMessageSource;

  /**
   * If true, templates will be deleted once applied. Useful, in some advanced template inheritance
   * use cases. Used by <code>{{#block}} helper</code>. Default is: false.
   * At any time you can override the default setup with:
   *
   * <pre>
   * {{#block "footer" delete-after-merge=true}}
   * </pre>
   */
  private boolean deletePartialAfterMerge;

  /**
   * Set variable formatters.
   */
  private Formatter[] formatters;

  /** Location of the handlebars.js file. */
  private String handlebarsJsFile;

  /** Template cache. */
  private TemplateCache templateCache = new HighConcurrencyTemplateCache();

  /** Charset. */
  private Charset charset = StandardCharsets.UTF_8;

  /** instance template loader. */
  private TemplateLoader templateLoader;

  /**
   * Creates a new {@link ReactiveHandlebarsViewResolver}.
   *
   * @param viewClass The view's class. Required.
   */
  public ReactiveHandlebarsViewResolver(final Class<? extends ReactiveHandlebarsView> viewClass) {
    setViewClass(viewClass);
    setPrefix(TemplateLoader.DEFAULT_PREFIX);
    setSuffix(TemplateLoader.DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link ReactiveHandlebarsViewResolver}.
   */
  public ReactiveHandlebarsViewResolver() {
    this(ReactiveHandlebarsView.class);
  }

  /**
   * Creates a new {@link ReactiveHandlebarsViewResolver} that utilizes the parameter handlebars
   * for the underlying template lifecycle management.
   *
   * @param handlebars The {@link Handlebars} instance used for template lifecycle management.
   *                   Required.
   */
  public ReactiveHandlebarsViewResolver(final Handlebars handlebars) {
    this(handlebars, ReactiveHandlebarsView.class);
  }

  /**
   * Creates a new {@link ReactiveHandlebarsViewResolver} that utilizes the parameter handlebars
   * for the underlying template lifecycle management.
   *
   * @param handlebars The {@link Handlebars} instance used for template lifecycle management.
   *                   Required.
   * @param viewClass  The view's class. Required.
   */
  public ReactiveHandlebarsViewResolver(final Handlebars handlebars,
                                        final Class<? extends ReactiveHandlebarsView> viewClass) {
    this(viewClass);
    this.handlebars = handlebars;
  }

  /**
   * Creates a new {@link Handlebars} object using the parameter {@link TemplateLoader}.
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
  protected TemplateLoader createTemplateLoader(final ApplicationContext context) {
    URLTemplateLoader springTemplateLoader = new SpringTemplateLoader(context);
    springTemplateLoader.setPrefix(getPrefix());
    springTemplateLoader.setSuffix(getSuffix());
    return springTemplateLoader;
  }

  /**
   * Configure the handlebars view.
   *
   * @param view The handlebars view.
   * @return The configured view.
   * @throws IOException If a resource cannot be loaded.
   */
  protected AbstractUrlBasedView configure(final ReactiveHandlebarsView view)
      throws IOException {
    String url = view.getUrl();
    if (StringUtils.isEmpty(url)) {
      throw new IllegalArgumentException("View URL must not be empty");
    }

    url = url.substring(getPrefix().length(),
        url.length() - getSuffix().length());

    try {
      view.setTemplate(handlebars.compile(url));
      view.setValueResolvers(valueResolvers);
    } catch (FileNotFoundException ex) {
      if (failOnMissingFile) {
        throw ex;
      }
      logger.debug("File not found: {}", url);
    }

    return view;
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
   * @throws IllegalArgumentException If the value resolvers are null or empty.
   */
  void setValueResolvers(final ValueResolver... valueResolvers) {
    if (ArrayUtils.isEmpty(valueResolvers)) {
      throw new IllegalArgumentException("At least one value-resolver must be present.");
    } else {
      this.valueResolvers = valueResolvers;
    }
  }

  /**
   * Set variable formatters.
   *
   * @param formatters Formatters to add.
   * @throws IllegalArgumentException If the formatters are null or empty.
   */
  public void setFormatters(final Formatter... formatters) {
    if (ArrayUtils.isEmpty(formatters)) {
      throw new IllegalArgumentException("At least one formatter must be present.");
    } else {
      this.formatters = formatters;
    }
  }

  /**
   * Set the handlebars.js location used it to compile/precompile template to JavaScript.
   * <p>
   * Using handlebars.js 2.x:
   * </p>
   *
   * <pre>
   *   Handlebars handlebars = new Handlebars()
   *      .handlebarsJsFile("handlebars-v2.0.0.js");
   * </pre>
   * <p>
   * Using handlebars.js 1.x:
   * </p>
   *
   * <pre>
   *   Handlebars handlebars = new Handlebars()
   *      .handlebarsJsFile("handlebars-v4.0.4.js");
   * </pre>
   *
   * Default handlebars.js is <code>handlebars-v4.0.4.js</code>.
   *
   * @param location A classpath location of the handlebar.js file.
   * @throws IllegalArgumentException If a location is null or empty string
   */
  public void setHandlebarsJsFile(final String location) {
    if (StringUtils.isEmpty(location)) {
      throw new IllegalArgumentException("Js file location is required");
    }
    this.handlebarsJsFile = location;
  }

  /**
   * True, if the view resolver should fail on missing files. Default is: true.
   *
   * @param failOnMissingFile True, if the view resolver should fail on
   *                          missing files. Default is: true.
   */
  public void setFailOnMissingFile(final boolean failOnMissingFile) {
    this.failOnMissingFile = failOnMissingFile;
  }

  /**
   * Register all the helpers in the map.
   *
   * @param helpers The helpers to be registered. Required.
   * @see Handlebars#registerHelper(String, Helper)
   */
  public void setHelpers(final Map<String, Helper<?>> helpers) {
    Objects.requireNonNull(helpers, "The helpers are required.");
    for (Map.Entry<String, Helper<?>> helper : helpers.entrySet()) {
      registry.registerHelper(helper.getKey(), helper.getValue());
    }
  }

  /**
   * Register all the helpers in the list. Each element of the list must be a helper source.
   *
   * @param helpers The helpers to be registered. Required.
   * @see Handlebars#registerHelpers(Class)
   * @see Handlebars#registerHelpers(Object)
   */
  public void setHelperSources(final List<?> helpers) {
    Objects.requireNonNull(helpers, "The helpers are required.");
    for (Object helper : helpers) {
      registry.registerHelpers(helper);
    }
  }

  /**
   * Same as {@link #setRegisterMessageHelper(boolean)} with a false argument. The message helper
   * wont be registered when you call this method.
   *
   * @return This handlebars view resolver.
   */
  public ReactiveHandlebarsViewResolver withoutMessageHelper() {
    setRegisterMessageHelper(false);
    return this;
  }

  /**
   * True, if the message helper (based on {@link MessageSource}) should be registered. Default is:
   * true.
   *
   * @param registerMessageHelper True, if the message helper (based on {@link MessageSource})
   *                              should be registered. Default is: true.
   */
  public void setRegisterMessageHelper(final boolean registerMessageHelper) {
    this.registerMessageHelper = registerMessageHelper;
  }

  /**
   * @param bindI18nToMessageSource If true, the i18n helpers will use a {@link MessageSource}
   *                                instead of a plain {@link ResourceBundle}. Default is: false.
   */
  public void setBindI18nToMessageSource(final boolean bindI18nToMessageSource) {
    this.bindI18nToMessageSource = bindI18nToMessageSource;
  }

  /**
   * If true, templates will be deleted once applied. Useful, in some advanced template inheritance
   * use cases. Used by <code>{{#block}} helper</code>. Default is: false.
   * At any time you can override the default setup with:
   *
   * <pre>
   * {{#block "footer" delete-after-merge=true}}
   * </pre>
   *
   * @param deletePartialAfterMerge True for clearing up templates once they got applied. Used by
   *                                <code>{{#block}} helper</code>.
   */
  public void setDeletePartialAfterMerge(final boolean deletePartialAfterMerge) {
    this.deletePartialAfterMerge = deletePartialAfterMerge;
  }

  /**
   * @param templateCache Set a template cache. Default is: {@link HighConcurrencyTemplateCache}.
   */
  public void setTemplateCache(final TemplateCache templateCache) {
    this.templateCache = templateCache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPrefix(final String prefix) {
    super.setPrefix(prefix);
    if (templateLoader != null) {
      templateLoader.setPrefix(prefix);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSuffix(final String suffix) {
    super.setSuffix(suffix);
    if (templateLoader != null) {
      templateLoader.setSuffix(suffix);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Decorator decorator(final String name) {
    return this.registry.decorator(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver registerDecorator(final String name,
                                                          final Decorator decorator) {
    registry.registerDecorator(name, decorator);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver setCharset(final Charset charset) {
    this.charset = charset;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver registerHelpers(final Object helperSource) {
    registry.registerHelpers(helperSource);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver registerHelpers(final Class<?> helperSource) {
    registry.registerHelpers(helperSource);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <C> Helper<C> helper(final String name) {
    return registry.helper(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Map.Entry<String, Helper<?>>> helpers() {
    return registry.helpers();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <H> ReactiveHandlebarsViewResolver registerHelper(final String name,
                                                           final Helper<H> helper) {
    registry.registerHelper(name, helper);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <H> ReactiveHandlebarsViewResolver registerHelperMissing(final Helper<H> helper) {
    registry.registerHelperMissing(helper);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver registerHelpers(final URI location) throws Exception {
    registry.registerHelpers(location);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver registerHelpers(final File input)
      throws Exception {
    registry.registerHelpers(input);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver registerHelpers(final String filename,
                                                        final Reader source)
      throws Exception {
    registry.registerHelpers(filename, source);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver registerHelpers(final String filename,
                                                        final InputStream source)
      throws Exception {
    registry.registerHelpers(filename, source);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReactiveHandlebarsViewResolver registerHelpers(final String filename,
                                                        final String source)
      throws IOException {
    registry.registerHelpers(filename, source);
    return this;
  }

  /**
   * {@inheritDoc}
   * Autoconfigure {@link Handlebars}, {@link I18nHelper}.
   */
  @Override
  public void afterPropertiesSet() {
    if (handlebars == null) {
      // Creates a new template loader.
      this.templateLoader = createTemplateLoader(getApplicationContext());

      // Creates a new handlebars object.
      handlebars = Objects.requireNonNull(createHandlebars(templateLoader),
          "A handlebars object is required.");
    }

    handlebars.with(registry);

    if (handlebarsJsFile != null) {
      handlebars.handlebarsJsFile(handlebarsJsFile);
    }

    if (formatters != null) {
      for (Formatter formatter : formatters) {
        handlebars.with(formatter);
      }
    }

    if (registerMessageHelper) {
      handlebars.registerHelper("message", new MessageSourceHelper(getApplicationContext()));
    }

    if (bindI18nToMessageSource) {
      I18nSource i18nSource = createI18nSource(getApplicationContext());

      I18nHelper.i18n.setSource(i18nSource);
      I18nHelper.i18nJs.setSource(i18nSource);
    }

    TemplateCache cache = handlebars.getCache();
    if (cache == NullTemplateCache.INSTANCE) {
      handlebars.with(templateCache);
    }

    handlebars.setDeletePartialAfterMerge(deletePartialAfterMerge);
    handlebars.setCharset(charset);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Class<?> requiredViewClass() {
    return ReactiveHandlebarsView.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AbstractUrlBasedView createView(final String viewName) {
    try {
      return configure((ReactiveHandlebarsView) super.createView(viewName));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
