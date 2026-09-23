/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.jknack.handlebars.internal;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Factory for creating a JavaScript {@link ScriptEngine}. By default tries Nashorn first, then
 * falls back to GraalJS. The engine can be forced via the {@code hbs.js_engine} system property.
 */
public final class ScriptEngineFactory {

  private ScriptEngineFactory() {}

  /**
   * Create a JavaScript engine. If the system property {@code hbs.js_engine} is set, only that
   * engine is tried. Otherwise tries Nashorn first, then GraalJS.
   *
   * @return a JavaScript ScriptEngine.
   * @throws IllegalStateException if no JavaScript engine is available or the requested engine is
   *     not found.
   */
  public static ScriptEngine create() {
    String requested = System.getProperty("hbs.js_engine");
    if (requested != null) {
      return createRequested(requested);
    }
    return createDefault();
  }

  private static ScriptEngine createDefault() {
    ScriptEngineManager manager = new ScriptEngineManager();

    ScriptEngine engine = manager.getEngineByName("nashorn");
    if (engine != null) {
      return engine;
    }

    engine = manager.getEngineByName("graal.js");
    if (engine != null) {
      configureGraalJS(engine);
      return engine;
    }

    throw new IllegalStateException(
        "No JavaScript engine found. Add either GraalJS or Nashorn to the classpath.");
  }

  private static ScriptEngine createRequested(String name) {
    ScriptEngineManager manager = new ScriptEngineManager();

    switch (name) {
      case "nashorn":
        ScriptEngine nashorn = manager.getEngineByName("nashorn");
        if (nashorn == null) {
          throw new IllegalStateException(
              "JavaScript engine 'nashorn' requested via hbs.js_engine but is not available."
                  + " Add org.openjdk.nashorn:nashorn-core to the classpath.");
        }
        return nashorn;

      case "graaljs":
        ScriptEngine graaljs = manager.getEngineByName("graal.js");
        if (graaljs == null) {
          throw new IllegalStateException(
              "JavaScript engine 'graaljs' requested via hbs.js_engine but is not available."
                  + " Add org.graalvm.js:js-scriptengine to the classpath.");
        }
        configureGraalJS(graaljs);
        return graaljs;

      default:
        throw new IllegalStateException(
            "Unknown hbs.js_engine value: '"
                + name
                + "'. Supported values are 'nashorn' and 'graaljs'.");
    }
  }

  private static void configureGraalJS(ScriptEngine engine) {
    Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
    bindings.put("polyglot.js.allowAllAccess", true);
    bindings.put("polyglot.js.nashorn-compat", true);
    bindings.put("polyglot.js.ecmascript-version", "2022");
  }

  /**
   * Check whether the given engine is Nashorn.
   *
   * @param engine the script engine.
   * @return true if the engine is Nashorn.
   */
  public static boolean isNashorn(ScriptEngine engine) {
    return engine.getFactory().getEngineName().toLowerCase().contains("nashorn");
  }
}
