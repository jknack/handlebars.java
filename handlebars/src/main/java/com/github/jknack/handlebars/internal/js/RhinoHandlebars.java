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
package com.github.jknack.handlebars.internal.js;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
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
   * The optimization level of rhino, default -1.
   * Please refer to https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino/Optimization
   */
  private int optimizationLevel = -1;

  /**
   * Better integration between java collections/arrays and js arrays. It check for data types
   * at access time and convert them when necessary.
   *
   * @author edgar
   */
  @SuppressWarnings("serial")
  private static class BetterNativeArray extends NativeArray {

    /** The context object. */
    private Context context;

    /** Internal state of array. */
    private Map<Object, Object> state = new HashMap<Object, Object>();

    /**
     * A JS array.
     *
     * @param array Array.
     * @param context Handlebars context.
     */
    public BetterNativeArray(final Object[] array, final Context context) {
      super(array);
      this.context = context;
    }

    /**
     * A JS collection.
     *
     * @param collection collection.
     * @param context Handlebars context.
     */
    public BetterNativeArray(final Collection<Object> collection, final Context context) {
      this(collection.toArray(new Object[collection.size()]), context);
    }

    @Override
    public Object get(final int index, final Scriptable start) {
      Object value = state.get(index);
      if (value != null) {
        return value;
      }
      value = super.get(index, start);
      value = toJsObject(value, context);
      state.put(index, value);
      return value;
    }

    @Override
    public String toString() {
      StringBuilder buff = new StringBuilder();
      String sep = ",";
      for (Object v : this) {
        buff.append(v).append(sep);
      }
      if (buff.length() > 0) {
        buff.setLength(buff.length() - sep.length());
      }
      return buff.toString();
    }
  }

  /**
   * Better integration between java objects and js object. It check for data types at access time
   * and convert them if necessary.
   *
   * @author edgar
   */
  @SuppressWarnings("serial")
  private static class BetterNativeObject extends NativeObject {

    /** Handlebars context. */
    private Context context;

    /** Internal state. */
    private Map<Object, Object> state = new HashMap<Object, Object>();

    /**
     * Creates a new {@link BetterNativeObject}.
     *
     * @param context Handlebars context.
     */
    public BetterNativeObject(final Context context) {
      this.context = context;
    }

    @Override
    public Object get(final String name, final Scriptable start) {
      Object value = state.get(name);
      if (value != null) {
        return value;
      }
      value = super.get(name, start);
      value = toJsObject(value, context);
      state.put(name, value);
      return value;
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
     * @param arg0 The helper first argument.
     * @param options The options object.
     * @return A string result.
     */
    Object apply(Object context, Object arg0, OptionsJs options);
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
     * @param options The {@link HandlebarsJs} options.
     */
    public OptionsJs(final Options options) {
      this.options = options;
      this.hash = hash(options.hash, options.context);
      this.params = new BetterNativeArray(options.params, options.context);
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
     * Apply the {@link #options#fn()} template using the provided context.
     *
     * @return The resulting text.
     * @throws IOException If a resource cannot be loaded.
     */
    public CharSequence fn() throws IOException {
      return options.fn();
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

    /**
     * Apply the {@link #options#inverse()} template using the provided context.
     *
     * @return The resulting text.
     * @throws IOException If a resource cannot be loaded.
     */
    public CharSequence inverse() throws IOException {
      return options.inverse();
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
   * Creates a new {@link RhinoHandlebars}.
   *
   * @param helperRegistry The handlebars object.
   * @param optimizationLevel The optimization level of rhino.
   */
  public RhinoHandlebars(final HelperRegistry helperRegistry, final int optimizationLevel) {
    super(helperRegistry);
    this.optimizationLevel = optimizationLevel;
  }

  /**
   * Register a helper in the helper registry.
   *
   * @param name The helper's name. Required.
   * @param helper The helper object. Required.
   */
  public void registerHelper(final String name, final JsHelper helper) {
    registry.registerHelper(name, new Helper<Object>() {
      @SuppressWarnings({"unchecked", "rawtypes" })
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        Object jsContext = toJsObject(options.context.model(), options.context);
        Object arg0 = context;
        Integer paramSize = options.data(Context.PARAM_SIZE);
        if (paramSize == 0) {
          arg0 = "___NOT_SET_";
        } else {
          arg0 = toJsObject(arg0, options.context);
        }
        Object result = helper.apply(jsContext, arg0, new OptionsJs(options));
        if (result instanceof NativeArray) {
          return new BetterNativeArray((List) result, options.context);
        }
        return result;
      }
    });
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
    ctx.setOptimizationLevel(optimizationLevel);
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

  /**
   * Convert a map to a JS Rhino object.
   *
   * @param map The map.
   * @param context Handlebars context.
   * @return A JS Rhino object.
   */
  private static NativeObject hash(final Map<?, Object> map, final Context context) {
    NativeObject hash = new BetterNativeObject(context);
    for (Entry<?, Object> prop : map.entrySet()) {
      hash.defineProperty(prop.getKey().toString(), prop.getValue(), NativeObject.READONLY);
    }
    return hash;
  }

  /**
   * Convert a Java Object to Js Object if necessary.
   *
   * @param object Source object.
   * @param parent Handlebars context.
   * @return A Rhino js object.
   */
  @SuppressWarnings({"unchecked", "rawtypes" })
  private static Object toJsObject(final Object object, final Context parent) {
    if (object == null) {
      return null;
    }
    if (object == Scriptable.NOT_FOUND) {
      return Scriptable.NOT_FOUND;
    }
    if (object instanceof Number) {
      return object;
    }
    if (object instanceof Boolean) {
      return object;
    }
    if (object instanceof CharSequence || object instanceof Character) {
      return object.toString();
    }

    if (Map.class.isInstance(object)) {
      return hash((Map) object, parent);
    } else if (Collection.class.isInstance(object)) {
      return new BetterNativeArray((Collection) object, parent);
    } else if (object.getClass().isArray()) {
      Object[] array = (Object[]) object;
      return new BetterNativeArray(array, parent);
    } else if (object instanceof NativeArray) {
      return new BetterNativeArray((NativeArray) object, parent);
    } else if (object instanceof Scriptable) {
      return object;
    }
    Context context = object instanceof Context
        ? (Context) object : Context.newContext(parent, object);
    return toJsObject(context);
  }

  /**
   * Convert a Java Object to Js Object if necessary.
   *
   * @param context Handlebars context.
   * @return A Rhino js object.
   */
  private static Object toJsObject(final Context context) {
    Map<String, Object> hash = new HashMap<String, Object>();
    for (Entry<String, Object> property : context.propertySet()) {
      hash.put(property.getKey(), property.getValue());
    }
    return hash(hash, context);
  }

}
