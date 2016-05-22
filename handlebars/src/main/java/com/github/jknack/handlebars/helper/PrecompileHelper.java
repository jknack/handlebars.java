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

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

/**
 * Precompile a template to JavaScript using handlebars.js.
 *
 * @author edgar.espina
 * @since 0.6.0
 */
public final class PrecompileHelper implements Helper<String> {

  /**
   * Wrap the precompiled function.
   *
   * @author edgar.espina
   */
  private enum JsWrapper {

    /**
     * Wrap a pre-compiled function as a anonymous and auto-executable function.
     */
    ANONYMOUS {
      @Override
      public void header(final String name, final StringBuilder buffer) {
        buffer.append("(function() {");
      }

      @Override
      public void tail(final StringBuilder buffer) {
        buffer.append("})();");
      }
    },

    /**
     * Wrap a pre-compiled function as a define function.
     */
    AMD {
      @Override
      public void header(final String name, final StringBuilder buffer) {
        buffer.append("define('").append(name)
            .append("', ['handlebars'], function(Handlebars) {");
      }

      @Override
      public void tail(final StringBuilder buffer) {
        buffer.append("  return template;\n");
        buffer.append("});");
      }
    },

    /**
     * Dont wrap anything.
     */
    NONE {
      @Override
      public void header(final String name, final StringBuilder buffer) {
      }

      @Override
      public void tail(final StringBuilder buffer) {
      }
    };

    /**
     * Append a header.
     *
     * @param name The template's name.
     * @param buffer The returning buffer.
     */
    public abstract void header(String name, StringBuilder buffer);

    /**
     * Append a tail.
     *
     * @param buffer The returning buffer.
     */
    public abstract void tail(StringBuilder buffer);

    /**
     * Register a template function.
     *
     * @param buffer The returning buffer.
     * @param name The template's name.
     * @param function The JavaScript function.
     */
    public void registerTemplate(final StringBuilder buffer, final String name,
        final String function) {
      buffer.append("\n  var template = Handlebars.template(").append(function).append(");\n");
      String[] namespaces = {"templates", "partials" };
      String separator = ";\n";
      for (String namespace : namespaces) {
        buffer.append("  var ").append(namespace).append(" = Handlebars.").append(namespace)
            .append(" = Handlebars.").append(namespace).append(" || {};\n");
        buffer.append("  ").append(namespace).append("['").append(name).append("'] = template")
            .append(separator);
      }
    }

    /**
     * Wrap the template function.
     *
     * @param name The template's name.
     * @param function The template's function.
     * @return A wrapped function.
     */
    public CharSequence wrap(final String name, final String function) {
      StringBuilder buffer = new StringBuilder();
      header(name, buffer);
      registerTemplate(buffer, name, function);
      tail(buffer);
      return buffer;
    };

    /**
     * Find the a wrap strategy.
     *
     * @param name The wrap's name.
     * @return The a wrap strategy or null.
     */
    public static JsWrapper wrapper(final String name) {
      for (JsWrapper wrapper : values()) {
        if (name.equalsIgnoreCase(wrapper.name())) {
          return wrapper;
        }
      }
      return null;
    }
  }

  /**
   * The default helper's name.
   */
  public static final String NAME = "precompile";

  /**
   * The default and shared instance.
   */
  public static final Helper<String> INSTANCE = new PrecompileHelper();

  /**
   * Not allowed.
   */
  private PrecompileHelper() {
  }

  @Override
  public Object apply(final String path, final Options options)
      throws IOException {
    notEmpty(path, "found: '%s', expected 'template path'", path);
    String wrapperName = options.hash("wrapper", "anonymous");
    final JsWrapper wrapper = JsWrapper.wrapper(wrapperName);
    notNull(wrapper, "found '%s', expected: '%s'",
        wrapperName, StringUtils.join(JsWrapper.values(), ", ").toLowerCase());

    Handlebars handlebars = options.handlebars;
    String name = path;
    if (name.startsWith("/")) {
      name = name.substring(1);
    }
    if (wrapper == JsWrapper.AMD) {
      name += handlebars.getLoader().getSuffix();
    }
    Template template = handlebars.compile(path);
    String precompiled = template.toJavaScript();
    return new Handlebars.SafeString(wrapper.wrap(name, precompiled));
  }

}
