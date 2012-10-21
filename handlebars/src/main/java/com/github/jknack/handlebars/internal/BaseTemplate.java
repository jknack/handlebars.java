/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.internal.RhinoExecutor.JsTask;

/**
 * Base class for {@link Template}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
abstract class BaseTemplate implements Template {

  /**
   * The line of this template.
   */
  protected int line;

  /**
   * The column of this template.
   */
  protected int column;

  /**
   * The file's name.
   */
  protected String filename;

  /**
   * A Handlebars.js lock.
   */
  private static final Object JS_LOCK = new Object();

  /**
   * A pre-compiled JavaScript function.
   */
  private String javaScript;

  /**
   * The handlebars.js file.
   */
  private static String handlebarsScript;

  /**
   * Handlerbars.js version.
   */
  private static final String HBS_FILE = "/handlebars-1.0.rc.1.js";

  static {
    handlebarsScript = handlebarsScript(HBS_FILE);
  }

  /**
   * Remove the child template.
   *
   * @param child The template to be removed.
   * @return True, if the child was removed
   */
  public abstract boolean remove(Template child);

  /**
   * {@inheritDoc}
   */
  @Override
  public final String apply(final Object context) throws IOException {
    return apply(wrap(context));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void apply(final Object context, final Writer writer)
      throws IOException {
    apply(wrap(context), writer);
  }

  @Override
  public String apply(final Context context) throws IOException {
    FastStringWriter writer = new FastStringWriter();
    apply(context, writer);
    return writer.toString();
  }

  @Override
  public void apply(final Context context, final Writer writer)
      throws IOException {
    notNull(writer, "A writer is required.");
    try {
      merge(wrap(context), writer);
    } catch (HandlebarsException ex) {
      throw ex;
    } catch (Exception ex) {
      String evidence = toString();
      String reason = ex.toString();
      String message =
          filename + ":" + line + ":" + column + ": "
              + reason + "\n";
      message += "    " + join(split(evidence, "\n"), "\n    ");
      HandlebarsError error =
          new HandlebarsError(filename, line, column, reason, evidence,
              message);
      HandlebarsException hex = new HandlebarsException(error, ex);
      // Override the stack-trace
      hex.setStackTrace(ex.getStackTrace());
      throw hex;
    }
  }

  /**
   * Wrap the candidate object as a Context, or creates a new context.
   *
   * @param candidate The candidate object.
   * @return A context.
   */
  private static Context wrap(final Object candidate) {
    if (candidate instanceof Context) {
      return (Context) candidate;
    }
    return Context.newContext(candidate);
  }

  /**
   * Merge a child template into the writer.
   *
   * @param context The scope object.
   * @param writer The writer.
   * @throws IOException If a resource cannot be loaded.
   */
  protected abstract void merge(final Context context, Writer writer)
      throws IOException;

  @Override
  public String toString() {
    return text();
  }

  /**
   * Set the file's name.
   *
   * @param filename The file's name.
   * @return This template.
   */
  public BaseTemplate filename(final String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Set the template position.
   *
   * @param line The line.
   * @param column The column.
   * @return This template.
   */
  public BaseTemplate position(final int line, final int column) {
    this.line = line;
    this.column = column;
    return this;
  }

  @Override
  public String toJavaScript() throws IOException {
    synchronized (JS_LOCK) {
      if (javaScript == null) {
        javaScript = RhinoExecutor.execute(new JsTask<String>() {
          @Override
          public String run(final Global global,
              final org.mozilla.javascript.Context context,
              final Scriptable scope) throws IOException {

            // Load handlebars.js
            context.evaluateString(scope, handlebarsScript, HBS_FILE, 1, null);

            scope.put("template", scope, text());

            String js = "Handlebars.precompile(template)";
            Object precompiled = context.evaluateString(scope, js, filename, 1,
                null);

            return (String) precompiled;
          }
        });
      }
      return javaScript;
    }
  }

  /**
   * Load the handlebars.js file from the given location.
   *
   * @param location The handlebars.js location.
   * @return The resource content.
   */
  private static String handlebarsScript(final String location) {
    InputStream in = BaseTemplate.class.getResourceAsStream(location);
    notNull(in, "Handlebars.js script not found at " + location);
    try {
      int ch = in.read();
      StringBuilder script = new StringBuilder();
      while (ch != -1) {
        script.append((char) ch);
        ch = in.read();
      }
      return script.toString();
    } catch (IOException ex) {
      throw new IllegalStateException("Unable to read file " + location);
    } finally {
      try {
        in.close();
      } catch (IOException ex) {
        throw new IllegalStateException("Unable to close file " + location);
      }
    }
  }
}
