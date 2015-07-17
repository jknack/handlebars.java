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

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.slf4j.Logger;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.internal.Files;
import com.github.jknack.handlebars.internal.JSEngine;
import com.github.jknack.handlebars.js.HandlebarsJs;

/**
 * An implementation of {@link HandlebarsJs} on top of javax.script API (JSR 223).
 *
 * @see {@link https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html}
 * @author edgar.espina.
 * @since 1.1.0
 */
public class JavaxScriptHandlebars extends HandlebarsJs {

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
    Object apply(Object contextProperties, Object arg0, OptionsJs options);
  }

  /**
   * The Handlebars.js options.
   *
   * @author edgar.espina
   * @since 1.1.0
   */
  public class OptionsJs {
    /**
     * Handlebars.java options.
     */
    private Options options;

    /**
     * The options hash as JSON.
     */
    public Object hash; // Map<String, Object> 

    /**
     * The helper params as JSON.
     */
    public Object params; // Object[]

    /**
     * Creates a new {@link HandlebarsJs} options.
     *
     * @param options The {@link HandlebarsJs} options.
     */
    public OptionsJs(final Options options) {
      this.options = options;
      this.hash = translateToJsObject(options.hash);
      this.params = translateToJsObject(options.params);
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
   * The logging system.
   */
  private static final Logger logger = getLogger(JavaxScriptHandlebars.class);

  /** Location of the handlebars.rhino.js file. */
  private static final String HANDLEBARS_HELPERS_REGISTRY_OVERRIDE_JS_FILE = "/helpers.rhino.js";

  /**
   * Creates a new {@link JavaxScriptHandlebars}.
   *
   * @param helperRegistry The handlebars object.
   */
  public JavaxScriptHandlebars(final HelperRegistry helperRegistry) {
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

        Object arg0 = null;

        Integer paramSize = options.data(Context.PARAM_SIZE);
        if (paramSize == 0) {
          arg0 = "___NOT_SET_";
        } else {
          arg0 = context;
        }

        Object result = helper.apply(translateToJsObject(contextProperties), translateToJsObject(arg0), new OptionsJs(options));
        if (result instanceof CharSequence) {
          return (CharSequence) result;
        }
        return result == null ? null : result.toString();
      }
    });
  }

  private Object translateToJsObject(Object object) {
    if (JSEngine.getInstance().engineKind == JSEngine.EngineKind.NASHORN) {
      // Nashorn already has proper translation of Java objects into comfortable JS objects,
      // see https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions#Nashornextensions-SpecialtreatmentofobjectsofspecificJavaclasses
      return object;
    } else {
      // assume Rhino:
      // invoke method from optional dependency handlebars-java7 via reflection, so this class does not contain an
      // an import statement for "com.github.jknack.handlebars.internal.js.JSConsumableObject"
      // and so can be loaded in a Java 8 JVM.
      // (JSConumableObject depends on jdk.rhino API not present in Java 8)
      Method method;
      try {
        Class<?> jsConsumableObjectClass = Class.forName("com.github.jknack.handlebars.internal.js.JSConsumableObject");
        try {
          method = jsConsumableObjectClass.getMethod("translateIfNecessary", Object.class);
        } catch (NoSuchMethodException e) {
          throw new RuntimeException("Handlebars internal error: check if com.github.jknack.handlebars.internal.js.JSConsumableObject.translateIfNecessary(Object) got renamed");
        } catch (SecurityException e) {
          throw new RuntimeException(e);
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Please add dependency handlebars-java7", e);
      }
      
      try {
        return method.invoke(null, object);
      } catch (Exception e) {
        throw new RuntimeException("Handlebars internal error: something went wrong in " + method, e);
      }
    }
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
      preHelpersRegistration(jsEngine);
      JSEngine.getInstance().getJsEngine().eval(source);
      postHelpersRegistration(jsEngine);
    } catch (ScriptException e) {
      logger.error("Registration of JS helpers in " + filename + "failed!" , e);
    }
  }

  /**
   * For subclasses to perform post-registration work.
   * @param jsEngine used for helpers registration
   */
  protected void postHelpersRegistration(final ScriptEngine jsEngine) {
  }

  /**
   * For subclasses to perform pre-registration work.
   * @param jsEngine used for helpers registration
   */
  protected void preHelpersRegistration(final ScriptEngine jsEngine) {
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
