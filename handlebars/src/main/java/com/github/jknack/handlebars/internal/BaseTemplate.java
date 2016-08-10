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
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.TagType;
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
   * The handlebars object. Required.
   */
  protected final Handlebars handlebars;

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
  private final Object jsLock = new Object();

  /**
   * A pre-compiled JavaScript function.
   */
  private String javaScript;

  /**
   * Creates a new {@link BaseTemplate}.
   *
   * @param handlebars A handlebars instance.
   */
  public BaseTemplate(final Handlebars handlebars) {
    this.handlebars = notNull(handlebars, "The handlebars can't be null.");
  }

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
    boolean decorate = decorate();
    try {
      if (decorate) {
        before(context, writer);
      }
      merge(context, writer);
    } catch (HandlebarsException ex) {
      throw ex;
    } catch (Exception ex) {
      String evidence = toString();
      String reason = ex.toString();
      String message = filename + ":" + line + ":" + column + ": "
          + reason + "\n";
      message += "    " + join(split(evidence, "\n"), "\n    ");
      HandlebarsError error = new HandlebarsError(filename, line, column, reason, evidence,
          message);
      HandlebarsException hex = new HandlebarsException(error, ex);
      // Override the stack-trace
      hex.setStackTrace(ex.getStackTrace());
      throw hex;
    } finally {
      if (decorate) {
        after(context, writer);
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
   * Notify that template is going to be processed.
   *
   * @param context The context object. Required.
   * @param writer The writer object. Required.
   * @throws IOException If a resource cannot be loaded.
   */
  public void before(final Context context, final Writer writer) throws IOException {
  }

  /**
   * Notify that template has been processed.
   *
   * @param context The context object. Required.
   * @param writer The writer object. Required.
   * @throws IOException If a resource cannot be loaded.
   */
  public void after(final Context context, final Writer writer) throws IOException {
  }

  /**
   * Merge a child template into the writer.
   *
   * @param context The scope object.
   * @param writer The writer.
   * @throws IOException If a resource cannot be loaded.
   */
  protected abstract void merge(final Context context, Writer writer) throws IOException;

  @Override
  public String toString() {
    return filename + ":" + line + ":" + column;
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

  @Override
  public String filename() {
    return filename;
  }

  @Override
  public int[] position() {
    return new int[]{line, column };
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
    return Proxy.newProxyInstance(rootType.getClassLoader(), new Class[]{rootType },
        new InvocationHandler() {

          private final Map<String, Object> attributes = new HashMap<>();
          private final Object[] emptyArgs = {};

          private boolean isDefault(final Method method) {
            return ((method.getModifiers()
                    & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC)
                    && method.getDeclaringClass().isInterface();
          }

          private Object invokeDefaultMethod(final Method method, final Class<?> lookupClass,
                                             final Object proxy, final Object ... args)
                  throws Throwable {
            // Jumping through these hoops is needed because calling unreflectSpecial requires that
            // the lookup instance have private access to the special caller. None of the static
            // factory methods for Lookup will give us an instance with the access modes we need,
            // so we work around it by calling the private constructor via reflection.
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(lookupClass, -1 /* trusted */)
                    .unreflectSpecial(method, lookupClass)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
          }

          @Override
          public Object invoke(final Object proxy, final Method method, final Object[] methodArgs)
              throws Throwable {

            Object[] args = methodArgs == null ? emptyArgs : methodArgs;

            if (isDefault(method)) {
              return invokeDefaultMethod(method, rootType, proxy, args);
            }

            String methodName = method.getName();

            if (args.length == 0 && methodName.equals("hashCode")) {
              return hashCode();
            }

            if (args.length == 0 && methodName.equals("toString")) {
              return String.format("TypeSafeTemplateProxy{interface=%s}", rootType.getSimpleName());
            }

            if (args.length == 1
                    && methodName.equals("equals")
                    && method.getParameterTypes()[0] == Object.class) {
              return args[0] == proxy;
            }

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
              if (args.length == 1 && attrName.length() > 0) {
                attributes.put(attrName, args[0]);
                if (TypeSafeTemplate.class.isAssignableFrom(method.getReturnType())) {
                  return proxy;
                }
                return null;
              }
            }
            String message = String.format(
                "No handler method for: '%s(%s)', expected method signature is: 'setXxx(value)'",
                methodName, join(args, ", "));
            throw new UnsupportedOperationException(message);
          }
        });
  }

  @Override
  public List<String> collect(final TagType... tagType) {
    isTrue(tagType.length > 0, "At least one tag type is required.");
    Set<String> tagNames = new LinkedHashSet<String>();
    for (TagType tt : tagType) {
      collect(tagNames, tt);
    }
    return new ArrayList<String>(tagNames);
  }

  /**
   * Child classes might want to check if they apply to the tagtype and append them self to the
   * result list.
   *
   * @param result The result list.
   * @param tagType The matching tagtype.
   */
  protected void collect(final Collection<String> result, final TagType tagType) {
  }

  @Override
  public List<String> collectReferenceParameters() {
    Set<String> paramNames = new LinkedHashSet<String>();
    collectReferenceParameters(paramNames);
    return new ArrayList<String>(paramNames);
  }

  /**
   * @param result The result list to add new parameters to.
   */
  protected void collectReferenceParameters(final Collection<String> result) {
  }

  @Override
  public String toJavaScript() {
    synchronized (jsLock) {
      if (javaScript == null) {
        javaScript = JSEngine.RHINO.toJavaScript(handlebars.handlebarsJsFile(), this);
      }
      return javaScript;
    }
  }

  /**
   * @return True if this template has decorators.
   */
  public boolean decorate() {
    return false;
  }

}
