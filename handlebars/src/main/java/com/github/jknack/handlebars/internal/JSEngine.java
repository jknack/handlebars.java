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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.github.jknack.handlebars.Template;

/**
 * Convert a template to JavaScript template (a.k.a precompiled template). Compilation is done by
 * handlebars.js and a JS Engine. For now, default and unique engine is Rhino.
 *
 * @author edgar
 *
 */
enum JSEngine {

  /**
   * The default JS Engine.
   */
  RHINO {
    @Override
    public String toJavaScript(final String hbslocation, final Template template) {
      Context ctx = null;
      try {
        ctx = newContext();

        Scriptable scope = newScope(hbslocation, ctx);
        scope.put("template", scope, template.text());

        String js = "Handlebars.precompile(template);";
        Object precompiled = ctx.evaluateString(scope, js, template.toString(), 1,
            null);

        return (String) precompiled;
      } finally {
        if (ctx != null) {
          org.mozilla.javascript.Context.exit();
        }
      }
    }

    /**
     * Creates a new scope where handlebars.js is present.
     *
     *@param hbslocation Location of the handlebars.js file.
     * @param ctx A rhino context.
     * @return A new scope where handlebars.js is present.
     */
    private Scriptable newScope(final String hbslocation, final Context ctx) {
      Scriptable sharedScope = sharedScope(hbslocation, ctx);
      Scriptable scope = ctx.newObject(sharedScope);
      scope.setParentScope(null);
      scope.setPrototype(sharedScope);

      return scope;
    }

    /**
     * Creates a new Rhino Context.
     *
     * @return A Rhino Context.
     */
    private Context newContext() {
      Context ctx = Context.enter();
      ctx.setOptimizationLevel(-1);
      ctx.setErrorReporter(new ToolErrorReporter(false));
      ctx.setLanguageVersion(Context.VERSION_1_8);
      return ctx;
    }

    /**
     * Creates a initialize the handlebars.js scope.
     *
     * @param hbslocation Location of the handlebars.js file.
     * @param ctx A rhino context.
     * @return A handlebars.js scope. Shared between executions.
     */
    private Scriptable sharedScope(final String hbslocation, final Context ctx) {
      ScriptableObject sharedScope = ctx.initStandardObjects();
      ctx.evaluateString(sharedScope, handlebarsScript(hbslocation), hbslocation, 1, null);
      return sharedScope;
    }

    /**
     * Load the handlebars.js file from the given location.
     *
     * @param location The handlebars.js location.
     * @return The resource content.
     */
    private String handlebarsScript(final String location) {
      try {
        return Files.read(location);
      } catch (IOException ex) {
        throw new IllegalArgumentException("Unable to read file: " + location, ex);
      }
    }
  };

  /**
   * Convert this template to JavaScript template (a.k.a precompiled template). Compilation is done
   * by handlebars.js and a JS Engine (usually Rhino).
   *
   * @param hbslocation Location of the handlebars.js file.
   * @param template The template to convert.
   * @return A pre-compiled JavaScript version of this template.
   */
  public abstract String toJavaScript(String hbslocation, Template template);
}
