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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.internal.Files;
import com.github.jknack.handlebars.js.HandlebarsJs;

/**
 * An implementation of {@link HandlebarsJs} on top of Rhino.
 *
 * @author edgar.espina.
 * @since 1.1.0
 */
public class RhinoHandlebars extends HandlebarsJs {

  /**
   * Wrap a {@link Context handlebars context} as a Rhino Js Object.
   *
   * @author edgar.espina
   * @since 1.1.0
   */
  @SuppressWarnings("serial")
  public static class JsContext extends ScriptableObject {

    /**
     * The {@link Handlebars} context.
     */
    private Context context;

    /**
     * Creates a new {@link JsContext}.
     *
     * @param context The {@link Handlebars} context.
     */
    public JsContext(final Context context) {
      this.context = context;
    }

    @Override
    public String getClassName() {
      return "Object";
    }

    @Override
    public Object get(final String name, final Scriptable start) {
      return context.get(name);
    }

    @Override
    public Object get(final int index, final Scriptable start) {
      return context.get("" + index);
    }

    @Override
    public Object[] getIds() {
      Set<Entry<String, Object>> propertySet = context.propertySet();
      Object[] ids = new Object[propertySet.size()];
      int idx = 0;
      for (Entry<String, Object> entry : propertySet) {
        ids[idx++] = entry.getKey();
      }
      return ids;
    }

  }

  /**
   * The JavaScript helper contract.
   *
   * @author edgar.espina
   * @since 1.1.0
   */
  public interface JsHelper {

    /**
     * Apply the helper to the context.
     *
     * @param context The context object.
     * @param options The options object.
     * @return A string result.
     */
    Object apply(Object context, OptionsJs options);
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
     * The options hash as JS Rhino object.
     */
    public NativeObject hash;

    /**
     * The helper params as JS Rhino object.
     */
    public NativeArray params;

    /**
     * Creates a new {@link HandlebarsJs} options.
     *
     * @param options The {@link Handlebars} options.
     */
    public OptionsJs(final Options options) {
      this.options = options;
      this.hash = hash(options.hash);
      this.params = new NativeArray(options.params);
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
   * @param handlebars The handlebars object.
   */
  public RhinoHandlebars(final Handlebars handlebars) {
    super(handlebars);
  }

  /**
   * Register a helper in the helper registry.
   *
   * @param name The helper's name. Required.
   * @param helper The helper object. Required.
   */
  public void registerHelper(final String name, final JsHelper helper) {
    handlebars.registerHelper(name, new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options) throws IOException {
        Object result = helper.apply(new JsContext(options.context), new OptionsJs(options));
        if (result instanceof CharSequence) {
          return (CharSequence) result;
        }
        return result == null ? null : result.toString();
      }
    });
  }

  @Override
  public void registerHelpers(final String filename, final String source) throws Exception {

    org.mozilla.javascript.Context ctx = null;
    try {
      ctx = newContext();

      Scriptable sharedScope = helpersEnvScope(ctx);
      sharedScope.put("Handlebars_java", sharedScope, this);
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

  /**
   * Convert a map to a JS Rhino object.
   *
   * @param map The map.
   * @return A JS Rhino object.
   */
  private static NativeObject hash(final Map<String, Object> map) {
    NativeObject hash = new NativeObject();
    for (Entry<String, Object> prop : map.entrySet()) {
      hash.defineProperty(prop.getKey(), prop.getValue(), NativeObject.READONLY);
    }
    return hash;
  }
}
