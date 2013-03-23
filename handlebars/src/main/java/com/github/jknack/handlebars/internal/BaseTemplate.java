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
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TypeSafeTemplate;

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
   * Handlerbars.js version.
   */
  private static final String HBS_FILE = "/handlebars-1.0.rc.1.js";

  /**
   * A shared scope with Handlebars.js objects.
   */
  private static ScriptableObject sharedScope;

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
    Context wrapped = wrap(context);
    try {
      merge(wrapped, writer);
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
    } finally {
      if (wrapped != context) {
        wrapped.destroy();
      }
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
  public <T, S extends TypeSafeTemplate<T>> S as(final Class<S> rootType) {
    notNull(rootType, "The rootType can't be null.");
    isTrue(rootType.isInterface(), "Not an interface: %s", rootType.getName());
    @SuppressWarnings("unchecked")
    S template = (S) newTypeSafeTemplate(rootType, this);
    return template;
  }

  @Override
  public <T> TypeSafeTemplate<T> as() {
    @SuppressWarnings("unchecked")
    TypeSafeTemplate<T> template = (TypeSafeTemplate<T>) newTypeSafeTemplate(
        TypeSafeTemplate.class, this);
    return template;
  }

  /**
   * Creates a new {@link TypeSafeTemplate}.
   *
   * @param rootType The target type.
   * @param template The target template.
   * @return A new {@link TypeSafeTemplate}.
   */
  private static Object newTypeSafeTemplate(final Class<?> rootType, final Template template) {
    return Proxy.newProxyInstance(template.getClass().getClassLoader(), new Class[]{rootType },
        new InvocationHandler() {
          private Map<String, Object> attributes = new HashMap<String, Object>();

          @Override
          public Object invoke(final Object proxy, final Method method, final Object[] args)
              throws Throwable {
            String methodName = method.getName();
            if ("apply".equals(methodName)) {
              Context context = Context.newBuilder(args[0])
                  .combine(attributes)
                  .build();
              attributes.clear();
              if (args.length == 2) {
                template.apply(context, (Writer) args[1]);
                return null;
              }
              return template.apply(context);
            }

            if (Modifier.isPublic(method.getModifiers()) && methodName.startsWith("set")) {
              String attrName = StringUtils.uncapitalize(methodName.substring("set".length()));
              if (args != null && args.length == 1 && attrName.length() > 0) {
                attributes.put(attrName, args[0]);
                if (TypeSafeTemplate.class.isAssignableFrom(method.getReturnType())) {
                  return proxy;
                }
                return null;
              }
            }
            String message = String.format(
                "No handler method for: '%s(%s)', expected method signature is: 'setXxx(value)'",
                methodName, args == null ? "" : join(args, ", "));
            throw new UnsupportedOperationException(message);
          }
        });
  }

  @Override
  public String toJavaScript() throws IOException {
    synchronized (JS_LOCK) {
      if (javaScript == null) {
        org.mozilla.javascript.Context ctx = null;
        try {
          ctx = newContext();

          Scriptable scope = newScope(ctx);
          scope.put("template", scope, text());

          String js = "Handlebars.precompile(template);";
          Object precompiled = ctx.evaluateString(scope, js, filename, 1,
              null);

          javaScript = (String) precompiled;
        } finally {
          if (ctx != null) {
            org.mozilla.javascript.Context.exit();
          }
        }
      }
      return javaScript;
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
   * Creates a new scope where handlebars.js is present.
   *
   * @param ctx A rhino context.
   * @return A new scope where handlebars.js is present.
   */
  private static Scriptable newScope(final org.mozilla.javascript.Context ctx) {
    Scriptable sharedScope = sharedScope(ctx);
    Scriptable scope = ctx.newObject(sharedScope);
    scope.setParentScope(null);
    scope.setPrototype(sharedScope);

    return scope;
  }

  /**
   * Creates a initialize the handlebars.js scope.
   *
   * @param ctx A rhino context.
   * @return A handlebars.js scope. Shared between executions.
   */
  private static Scriptable
      sharedScope(final org.mozilla.javascript.Context ctx) {
    if (sharedScope == null) {
      sharedScope = ctx.initStandardObjects();
      ctx.evaluateString(sharedScope, handlebarsScript(HBS_FILE), HBS_FILE, 1,
          null);
    }
    return sharedScope;
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
