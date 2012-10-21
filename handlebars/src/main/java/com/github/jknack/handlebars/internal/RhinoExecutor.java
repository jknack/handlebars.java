/**
 * Copyright (c) 2012 Edgar Espina
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
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.shell.Global;

/**
 * Utility class for running javascript code in rhino.
 *
 * @author edgar.espina
 * @since 0.6.0
 */
final class RhinoExecutor {

  /**
   * The JavaScript task.
   *
   * @author edgar.espina
   * @since 0.2.3
   * @param <V> The resulting value.
   */
  public interface JsTask<V> {

    /**
     * Execute a JavaScript task.
     *
     * @param global Define some global functions particular to the shell. Note
     *        that these functions are not part of ECMA.
     * @param context The excecution context.
     * @param scope The script scope.
     * @return A resulting value.
     * @throws IOException If something goes wrong.
     */
    V run(Global global, Context context, Scriptable scope) throws IOException;
  }

  /**
   * Not allowed.
   */
  private RhinoExecutor() {
  }

  /**
   * Execute a JavaScript task using Rhino.
   *
   * @param task The JavaScript task.
   * @return The resulting value.
   * @param <V> The resulting value.
   * @throws IOException If something goes wrong.
   */
  public static <V> V execute(final JsTask<V> task) throws IOException {
    Context context = null;
    try {
      context = Context.enter();
      context.setOptimizationLevel(-1);
      context.setErrorReporter(new ToolErrorReporter(false));
      context.setLanguageVersion(Context.VERSION_1_8);

      Global global = new Global();
      global.init(context);

      Scriptable scope = context.initStandardObjects(global);

      return task.run(global, context, scope);
    } finally {
      if (context != null) {
        Context.exit();
      }
    }
  }

}
