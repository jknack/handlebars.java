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
package com.github.jknack.handlebars.internal.js;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.internal.Files;
import com.github.jknack.handlebars.js.HandlebarsJs;
import com.google.gson.Gson;

/**
 * An implementation of {@link HandlebarsJs} on top of Rhino.
 *
 * @author edgar.espina.
 * @since 1.1.0
 */
public class RhinoHandlebars extends HandlebarsJs {

  /**
   * The JavaScript helper contract. Implemented in Javascript in helpers.rhino.js
   *
   * @author edgar.espina
   * @since 1.1.0
   */
  public interface JsHelper {

    /**
     * Apply the helper to the context.
     *
     * @param context The context object.
     * @param arg0 The helper first argument, if it is a simple object (i.e. not an array, collection or map).
     * @param complexArg0Json The helper first argument as JSON, if it is a an array, collection or map
     * @param options The options object.
     * @return A string result.
     */
    Object apply(String contextJson, String complexArg0Json, Object simpleArg0, OptionsJs options);
  }

  /**
   * The Handlebars.js options.
   *
   * @author edgar.espina
   * @since 1.1.0
   */
  public static class OptionsJs {
    /**
     * Handlebars.java options.
     */
    private Options options;

    /**
     * The options hash as JSON
     */
    public String hashJson; //Map<String, Object>

    /**
     * The helper params as JSON
     */
    public String paramsJson; // Object[]

    /**
     * Creates a new {@link HandlebarsJs} options.
     *
     * @param options The {@link HandlebarsJs} options.
     */
    public OptionsJs(final Options options) {
      this.options = options;
      this.hashJson = toJson(options.hash);
      this.paramsJson = toJson(options.params);
    }

    /**
     * Apply the {@link #options#fn(Object)} template using the provided context.
     *
     * @param context The context to use.
     * @return The resulting text.
     * @throws IOException If a resource cannot be loaded.
     */
    public CharSequence fn(final Object context) throws IOException {
      return options.fn(context);
    }

    /**
     * Apply the {@link #options#inverse(Object)} template using the provided context.
     *
     * @param context The context to use.
     * @return The resulting text.
     * @throws IOException If a resource cannot be loaded.
     */
    public CharSequence inverse(final Object context) throws IOException {
      return options.inverse(context);
    }
  }

  /**
   * The JavaScript helpers environment for Rhino.
   */
  private static final String HELPERS_ENV = envSource("/helpers.rhino.js");

  /**
   * Creates a new {@link RhinoHandlebars}.
   *
   * @param helperRegistry The handlebars object.
   */
  public RhinoHandlebars(final HelperRegistry helperRegistry) {
    super(helperRegistry);
  }

  /**
   * Register a helper in the helper registry.
   *
   * @param name The helper's name. Required.
   * @param helper The helper object. Required.
   */
  public void registerHelper(final String name, final JsHelper helper) {
    registry.registerHelper(name, new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options) throws IOException {
        Map<String, Object>contextProperties = contextPropertiesToMap(options.context);

        String jsContextJson = toJson(contextProperties);
        
        String complexArg0Json = null;
        Object arg0 = null;
        
        Integer paramSize = options.data(Context.PARAM_SIZE);
        if (paramSize == 0) {
          arg0 = "___NOT_SET_";
        } else {
          if (isSimpleObject(context)) {
            arg0 = context;
          } else if (context != null) {
            complexArg0Json = toJson(context);
          } // else: keep arg0 = null
        }
        
        Object result = helper.apply(jsContextJson, complexArg0Json, arg0, new OptionsJs(options));
        if (result instanceof CharSequence) {
          return (CharSequence) result;
        }
        return result == null ? null : result.toString();
      }
    });
  }

  private static String toJson(Object object) {
    return new Gson().toJson(object);
  }
  
  /**
   * Returns true for anything that we don' need to serialize as JSON.
   * @param object
   * @return
   */
  private boolean isSimpleObject(Object object) {
    
    if (object instanceof Number) {
      return true;
    }
    if (object instanceof Boolean) {
      return true;
    }
    if (object instanceof CharSequence || object instanceof Character) {
      return true;
    }
    if (object instanceof Scriptable) {
      return true;
    }  
    return false;
  }
  
  @Override
  public void registerHelpers(final String filename, final String source) throws Exception {

    org.mozilla.javascript.Context ctx = null;
    try {
      ctx = newContext();

      Scriptable sharedScope = helpersEnvScope(ctx);
      Scriptable scope = ctx.newObject(sharedScope);
      scope.setParentScope(null);
      scope.setPrototype(sharedScope);

      ctx.evaluateString(scope, source, filename, 1, null);
    } finally {
      if (ctx != null) {
        org.mozilla.javascript.Context.exit();
      }
    }
  }

  /**
   * Creates a new Rhino Context.
   *
   * @return A Rhino Context.
   */
  private org.mozilla.javascript.Context newContext() {
    org.mozilla.javascript.Context ctx = org.mozilla.javascript.Context.enter();
    ctx.setOptimizationLevel(-1);
    ctx.setErrorReporter(new ToolErrorReporter(false));
    ctx.setLanguageVersion(org.mozilla.javascript.Context.VERSION_1_8);
    return ctx;
  }

  /**
   * Creates a initialize the helpers.rhino.js scope.
   *
   * @param ctx A rhino context.
   * @return A handlebars.js scope. Shared between executions.
   */
  private Scriptable helpersEnvScope(final org.mozilla.javascript.Context ctx) {
    Scriptable env = ctx.initStandardObjects();
    env.put("Handlebars_java", env, this);
    ctx.evaluateString(env, HELPERS_ENV, "helpers.rhino.js", 1, null);
    return env;
  }

  /**
   * Load the helper environment.
   *
   * @param location The classpath location.
   * @return The helper environment.
   */
  private static String envSource(final String location) {
    try {
      return Files.read(location);
    } catch (IOException ex) {
      throw new IllegalStateException("Unable to read " + location, ex);
    }
  }




  private static Map<String, Object> contextPropertiesToMap(final Context context) {
    Map<String, Object> hash = new HashMap<String, Object>();
    for (Entry<String, Object> property : context.propertySet()) {
      hash.put(property.getKey(), property.getValue());
    }
    return hash;
  }
}
