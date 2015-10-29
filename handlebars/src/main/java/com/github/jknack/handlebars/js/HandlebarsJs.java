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
package com.github.jknack.handlebars.js;

import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.Constructor;

import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.internal.JSEngine;
import com.github.jknack.handlebars.internal.js.JavaxScriptHandlebars;

/**
 * The main motivation of {@link HandlebarsJs} is the ability of reuse JavaScript helpers in the
 * server and the client.
 *
 * @author edgar.espina
 * @since 1.1.0
 */
public abstract class HandlebarsJs {

  /**
   * The handlebars instance. Required.
   */
  protected final HelperRegistry registry;

  /**
   * Creates a new {@link HelperRegistry} object.
   *
   * @param helperRegistry The {@link HelperRegistry} instance. Required.
   */
  public HandlebarsJs(final HelperRegistry helperRegistry) {
    this.registry = notNull(helperRegistry, "The helper registry is required.");
  }

  /**
   * <p>
   * Register helpers from a JavaScript source.
   * </p>
   * <p>
   * A JavaScript source file looks like:
   * </p>
   *
   * <pre>
   *  Handlebars.registerHelper('hey', function (context) {
   *    return 'Hi ' + context.name;
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, options) {
   *    return 'Hi ' + context.name + options.hash['x'];
   *  });
   *  ...
   *  Handlebars.registerHelper('hey', function (context, p1, p2, options) {
   *    return 'Hi ' + context.name + p1 + p2 + options.hash['x'];
   *  });
   *  ...
   * </pre>
   *
   * To keep your helpers reusable between server and client avoid DOM manipulation.
   *
   * @param filename The file name (just for debugging purpose). Required.
   * @param source The JavaScript source. Required.
   * @throws Exception If the JavaScript helpers can't be registered.
   */
  public abstract void registerHelpers(String filename, String source) throws Exception;

  /**
   * Creates a {@link HandlebarsJs} object.
   *
   * @param helperRegistry The helperRegistry object. Required.
   * @param javaxScriptHandlebarsClass of {@link JavaxScriptHandlebars} to instantiate.
   *  Optional. Defaults to {@link JavaxScriptHandlebars}.
   * @return A new {@link HandlebarsJs} object.
   */
  public static HandlebarsJs create(final HelperRegistry helperRegistry,
      final Class<? extends JavaxScriptHandlebars> javaxScriptHandlebarsClass) {
    if (JSEngine.getInstance().getJsEngine() != null) {
      if (javaxScriptHandlebarsClass == null) {
        return new JavaxScriptHandlebars(helperRegistry);
      } else {
        try {
          // poor man's factory mechanism via Reflection
          Constructor<? extends JavaxScriptHandlebars> constructor =
              javaxScriptHandlebarsClass.getConstructor(HelperRegistry.class);
          return constructor.newInstance(helperRegistry);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    } else {
      return new HandlebarsJs(helperRegistry) {
        @Override
        public void registerHelpers(final String filename, final String source) throws Exception {
          throw new IllegalStateException("Neither Rhino nor Nashorn is on the classpath");
        }
      };
    }
  }
}
