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

import com.github.jknack.handlebars.internal.Throwing;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Decorator;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.internal.Files;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Default implementation of {@link HelperRegistry}.
 *
 * @author edgar
 * @since 1.2.0
 */
public class DefaultHelperRegistry implements HelperRegistry {

  /** The logging system. */
  private final Logger logger = LoggerFactory.getLogger(HelperRegistry.class);

  /**
   * The JavaScript helpers environment for Rhino.
   */
  private static final String HELPERS_ENV;

  static {
    String file = "/helpers.nashorn.js";
    try {
      HELPERS_ENV = Files.read(file, StandardCharsets.UTF_8);
    } catch (IOException x) {
      throw new IllegalStateException("File not found: " + file, x);
    }
  }

  /**
   * The helper registry.
   */
  private final Map<String, Helper<?>> helpers = new HashMap<>();

  /** Decorators. */
  private final Map<String, Decorator> decorators = new HashMap<>();

  /** Charset. */
  private Charset charset = StandardCharsets.UTF_8;

  /** Engine. */
  private ScriptEngine engine;

  /** ES6's let/const declaration Pattern. */
  private Pattern es6VarPattern = Pattern.compile("(?:^|[\\s(;])(let|const)\\s+");

  {
    // make sure default helpers are registered
    registerBuiltinsHelpers(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <C> Helper<C> helper(final String name) {
    notEmpty(name, "A helper's name is required.");
    return (Helper<C>) helpers.get(name);
  }

  @Override
  public <H> HelperRegistry registerHelper(final String name, final Helper<H> helper) {
    notEmpty(name, "A helper's name is required.");
    notNull(helper, "A helper is required.");

    Helper<?> oldHelper = helpers.put(name, helper);
    if (oldHelper != null) {
      logger.debug("Helper '{}' has been replaced by '{}'", name, helper);
    }
    return this;
  }

  @Override
  public <H> HelperRegistry registerHelperMissing(final Helper<H> helper) {
    return registerHelper(Handlebars.HELPER_MISSING, helper);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public HelperRegistry registerHelpers(final Object helperSource) {
    notNull(helperSource, "The helper source is required.");
    isTrue(!(helperSource instanceof String), "java.lang.String isn't a helper source.");
    try {
      if (helperSource instanceof File) {
        // adjust to File version
        return registerHelpers((File) helperSource);
      } else if (helperSource instanceof URI) {
        // adjust to URI version
        return registerHelpers((URI) helperSource);
      } else if (helperSource instanceof Class) {
        // adjust to Class version
        return registerHelpers((Class) helperSource);
      }
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new IllegalArgumentException("Can't register helpers", ex);
    }
    registerDynamicHelper(helperSource, helperSource.getClass());
    return this;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public HelperRegistry registerHelpers(final Class<?> helperSource) {
    notNull(helperSource, "The helper source is required.");
    if (Enum.class.isAssignableFrom(helperSource)) {
      Enum[] helpers = ((Class<Enum>) helperSource).getEnumConstants();
      for (Enum helper : helpers) {
        isTrue(helper instanceof Helper, "'%s' isn't a helper.", helper.name());
        registerHelper(helper.name(), (Helper) helper);
      }
    } else {
      registerDynamicHelper(null, helperSource);
    }
    return this;
  }

  @Override
  public HelperRegistry registerHelpers(final URI location) throws Exception {
    return registerHelpers(location.getPath(), Files.read(location.toString(), charset));
  }

  @Override
  public HelperRegistry registerHelpers(final File input) throws Exception {
    return registerHelpers(input.getAbsolutePath(), Files.read(input, charset));
  }

  @Override
  public HelperRegistry registerHelpers(final String filename, final Reader source)
      throws Exception {
    return registerHelpers(filename, Files.read(source));
  }

  @Override
  public HelperRegistry registerHelpers(final String filename, final InputStream source)
      throws Exception {
    return registerHelpers(filename, Files.read(source, charset));
  }

  @Override
  public HelperRegistry registerHelpers(final String filename, final String source)
      throws IOException {
    notNull(filename, "The filename is required.");
    notEmpty(source, "The source is required.");
    ScriptEngine engine = engine();
    Throwing.run(() -> engine.eval(adaptES6Literals(source)));
    return this;
  }

  @Override
  public Set<Entry<String, Helper<?>>> helpers() {
    return this.helpers.entrySet();
  }

  /**
   * Since nashorn doesn't yet supports the ES6's "const" or "let" literals.
   *  This method adapts the given helper source written in ES6 to work
   *  with nashorn (by converting let/const to var).
   *
   * @param source the helper source.
   * @return the adapted helper source.
   **/
  private String adaptES6Literals(final String source) {
    Matcher m = es6VarPattern.matcher(source);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
        StringBuffer buf = new StringBuffer(m.group());
        buf.replace(m.start(1) - m.start(), m.end(1) - m.start(), "var");
        m.appendReplacement(sb, buf.toString());
    }
    return m.appendTail(sb).toString();
  }

  /**
   * <p>
   * Register all the helper methods for the given helper source.
   * </p>
   *
   * @param source The helper source.
   * @param clazz The helper source class.
   */
  private void registerDynamicHelper(final Object source, final Class<?> clazz) {
    if (clazz != Object.class) {
      Set<String> overloaded = new HashSet<>();
      // Keep backing up the inheritance hierarchy.
      Method[] methods = clazz.getDeclaredMethods();
      for (Method method : methods) {
        boolean isPublic = Modifier.isPublic(method.getModifiers());
        if (isPublic) {
          boolean isStatic = Modifier.isStatic(method.getModifiers());
          if (source != null || isStatic) {
            HelperFunction annotation = method.getAnnotation(HelperFunction.class);
            String helperName = annotation != null ? annotation.value() : method.getName();
            isTrue(overloaded.add(helperName), "name conflict found: " + helperName);
            registerHelper(helperName, new MethodHelper(method, source));
          }
        }
      }
    }
  }

  /**
   * Register built-in helpers.
   *
   * @param registry The handlebars instance.
   */
  private static void registerBuiltinsHelpers(final HelperRegistry registry) {
    registry.registerHelper(WithHelper.NAME, WithHelper.INSTANCE);
    registry.registerHelper(IfHelper.NAME, IfHelper.INSTANCE);
    registry.registerHelper(UnlessHelper.NAME, UnlessHelper.INSTANCE);
    registry.registerHelper(EachHelper.NAME, EachHelper.INSTANCE);
    registry.registerHelper(EmbeddedHelper.NAME, EmbeddedHelper.INSTANCE);
    registry.registerHelper(BlockHelper.NAME, BlockHelper.INSTANCE);
    registry.registerHelper(PartialHelper.NAME, PartialHelper.INSTANCE);
    registry.registerHelper(PrecompileHelper.NAME, PrecompileHelper.INSTANCE);
    registry.registerHelper("i18n", I18nHelper.i18n);
    registry.registerHelper("i18nJs", I18nHelper.i18nJs);
    registry.registerHelper(LookupHelper.NAME, LookupHelper.INSTANCE);
    registry.registerHelper(LogHelper.NAME, LogHelper.INSTANCE);

    // decorator
    registry.registerDecorator("inline", InlineDecorator.INSTANCE);
  }

  @Override
  public Decorator decorator(final String name) {
    notEmpty(name, "A decorator's name is required.");
    return decorators.get(name);
  }

  @Override
  public HelperRegistry registerDecorator(final String name, final Decorator decorator) {
    notEmpty(name, "A decorator's name is required.");
    notNull(decorator, "A decorator is required.");

    Decorator old = decorators.put(name, decorator);
    if (old != null) {
      logger.warn("Decorator '{}' has been replaced by '{}'", name, decorator);
    }
    return this;
  }

  @Override public DefaultHelperRegistry setCharset(final Charset charset) {
    this.charset = notNull(charset, "Charset required.");
    return this;
  }

  /**
   * @return Nashorn engine.
   */
  private ScriptEngine engine() {
    synchronized (this) {
      if (this.engine == null) {
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");

        this.engine.put("Handlebars_java", this);

        Throwing.run(() -> engine.eval(HELPERS_ENV));
      }
      return this.engine;
    }
  }
}
