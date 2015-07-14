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

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.internal.Files;
import com.github.jknack.handlebars.internal.JSEngine;
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
     * @param contextJson The context object as JSON.
     * @param simpleArg0 The helper first argument, if it is a simple object.
     * @param complexArg0Json The helper first argument as JSON,
     *    if it is a an array, collection or map
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
     * The options hash as JSON.
     */
    public String hashJson; //Map<String, Object>

    /**
     * The helper params as JSON.
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

  /** Location of the handlebars.rhino.js file. */
  private static final String HANDLEBARS_HELPERS_REGISTRY_OVERRIDE_JS_FILE = "/helpers.rhino.js";

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

  /**
   * Serialize given object to JSON String.
   *
   * @param object to serialize
   * @return JSON String
   */
  private static String toJson(final Object object) {
    return new Gson().toJson(object);
  }

  /**
   * Returns true for anything that we don' need to serialize as JSON.
   *
   * @param object to analyze
   * @return whether object is Number, Boolean, String
   */
  private boolean isSimpleObject(final Object object) {

    if (object instanceof Number) {
      return true;
    }
    if (object instanceof Boolean) {
      return true;
    }
    if (object instanceof CharSequence || object instanceof Character) {
      return true;
    }
//    if (object instanceof Scriptable) {
//      return true;
//    }
    return false;
  }

  @Override
  public void registerHelpers(final String filename, final String source) throws Exception {

    ScriptEngine jsEngine = JSEngine.getInstance().getJsEngine();
    String handlebarsHelpersRegistryOverrideScript =
        Files.read(HANDLEBARS_HELPERS_REGISTRY_OVERRIDE_JS_FILE);
    try {
      // this script redefines Handlebars.registerHelper(), so JS helper
      // registration is happening against our Java helper registry
      jsEngine.eval(handlebarsHelpersRegistryOverrideScript);
    } catch (ScriptException e) {
      throw new RuntimeException(e);
    }

    jsEngine.put("Handlebars_java", this);
    try {
      JSEngine.getInstance().getJsEngine().eval(source);
    } catch (ScriptException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Turn a given context's properties into a Map.
   * @param context given
   * @return the Map
   */
  private static Map<String, Object> contextPropertiesToMap(final Context context) {
    Map<String, Object> hash = new HashMap<String, Object>();
    for (Entry<String, Object> property : context.propertySet()) {
      hash.put(property.getKey(), property.getValue());
    }
    return hash;
  }
}
