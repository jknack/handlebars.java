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
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.github.jknack.handlebars.Template;

/**
 * Convert a template to JavaScript template (a.k.a precompiled template). Compilation is done by
 * handlebars.js and a JS Engine.
 * Keeps handlebars.js as {@link CompiledScript} object per handlebars.js file, so the handlebars.js
 * is compiled only once, and doesn't need to be interpreted by the underlying {@link ScriptEngine}.
 *
 * @author edgar
 *
 */
public final class JSEngine {


  /**
   * The Javascript engine (defaults to Rhino with Java 7, and Nashorn with Java 8).
   */
  private final ScriptEngine jsEngine;

  /**
   * Optimize execution by compiling the HANDLEBARS_JS_FILE only once.
   */
  private final Map<String, CompiledScript> precompiledHandlebars
    = new HashMap<String, CompiledScript>();


  /**
   * Singleton instance.
   */
  private static JSEngine instance;

  /**
   * Obtain singleton instance.
   * @return the NewJsEngine
   */
  public static synchronized JSEngine getInstance() {
    if (instance == null) {
      instance = new JSEngine();
    }
    return instance;
  }

  /**
   * Nashorn or Rhino.
   */
  private final EngineKind engineKind;

  /**
   * Nashorn or Rhino.
   * @return the kind of engine we found
   */
  public EngineKind getEngineKind() {
    return engineKind;
  }

  /**
   * Constructor.
   */
  private JSEngine() {
    jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    String engineClassName = jsEngine.getClass().getName();
    if (engineClassName.toLowerCase().contains("nashorn")) {
      engineKind = EngineKind.NASHORN;
    } else if (engineClassName.toLowerCase().contains("rhino")) {
      engineKind = EngineKind.RHINO;
    } else {
      throw new RuntimeException(
          "Javascript engine must be either Rhino or Nashorn, but isn't: " + engineClassName);
    }
  }

  /**
   * Nashorn or Rhino.
   */
  public enum EngineKind {
    /**
     * Engine coming either with Java 7 JRE, or as a separate dependency e.g. on JDK 1.6.
     */
    RHINO,
    /**
     * Engine coming with Java 8.
     */
    NASHORN
  }

  /**
   * Find cached {@link CompiledScript} for hbsLocation, or compile and cache it.
   * @param hbsLocation path to handlebars.js file
   * @return compiled handlebars.js
   */
  private synchronized CompiledScript getPrecompiledHandlebarsJs(final String hbsLocation) {
    if (precompiledHandlebars.get(hbsLocation) == null) {
      Compilable compilable = (Compilable) jsEngine;
      try {
        precompiledHandlebars.put(hbsLocation,
            compilable.compile(readScript(hbsLocation) + "\nHandlebars.precompile(template);"));
      } catch (ScriptException e) {
        throw new RuntimeException(e);
      }
    }
    return precompiledHandlebars.get(hbsLocation);
  }

  /**
   * Convert this template to JavaScript template (a.k.a precompiled template). Compilation is done
   * by handlebars.js and a JS Engine.
   *
   * @param hbsLocation of handlebars.js file
   * @param template The template to convert.
   * @return A pre-compiled JavaScript version of this template.
   */
  // @Override
  public String toJavaScript(final String hbsLocation, final Template template) {

    // isolate Handlebars template compilation runs by providing separate contexts:
    // it turned out during tests execution that subsequent execution with two different
    // handlebars.js version files results in the first being used also on the second
    // run.
    // For setting up contexts in ScriptingEngine see
    // https://wiki.openjdk.java.net/display/Nashorn/Nashorn+jsr223+engine+notes
    ScriptContext myContext = new SimpleScriptContext();
    Bindings bindings = getJsEngine().createBindings();
    bindings.put("template", template.text());
    myContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    jsEngine.setContext(myContext);
    jsEngine.put("template", template.text());
    try {
      return (String) getPrecompiledHandlebarsJs(hbsLocation).eval();
    } catch (ScriptException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Load the handlebars.js file from the given location.
   *
   * @param location The handlebars.js location.
   * @return The resource content.
   */
  private static String readScript(final String location) {
    try {
      return Files.read(location);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Unable to read file: " + location, ex);
    }
  }

  /**
   * @return the {@link ScriptEngine} in use
   */
  public ScriptEngine getJsEngine() {
    return jsEngine;
  }

}
