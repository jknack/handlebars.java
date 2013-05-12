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
package com.github.jknack.handlebars.helper;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Wrap a method as Handlebars helper.
 *
 * @author edgar.espina
 * @see Handlebars#registerHelpers(Object)
 * @see Handlebars#registerHelpers(Class)
 */
public class MethodHelper implements Helper<Object> {

  /**
   * The source or instance object. Might be null.
   */
  private Object source;

  /**
   * The method to invoke. Required.
   */
  private Method method;

  /**
   * Creates a new {@link MethodHelper}.
   *
   * @param method The method to invoke. Required.
   * @param source The source or instance object. Might be null.
   */
  public MethodHelper(final Method method, final Object source) {
    this.method = notNull(method, "A helper method is required.");
    this.source = source;
  }

  @Override
  public CharSequence apply(final Object context, final Options options) throws IOException {
    Class<?>[] paramTypes = method.getParameterTypes();
    Object[] args = new Object[paramTypes.length];
    // collect the parameters
    int pidx = 0;
    for (int i = 0; i < paramTypes.length; i++) {
      Class<?> paramType = paramTypes[i];
      Object ctx = i == 0 ? context : null;
      Options opts = i == paramTypes.length - 1 ? options : null;
      Object candidate = options.param(pidx, null);
      Object arg = argument(paramType, candidate, ctx, opts);
      args[i] = arg;
      if (candidate == arg) {
        pidx++;
      }
    }
    try {
      return (CharSequence) method.invoke(source, args);
    } catch (InvocationTargetException ex) {
      throw launderThrowable(ex.getCause());
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException("could not execute helper: " + method.getName(), ex);
    }
  }

  /**
   * Return a runtime exception or throw an {@link IOException}.
   *
   * @param cause The invocation cause.
   * @return A runtime exception or throw an {@link IOException}.
   * @throws IOException If the cause is an {@link IOException}.
   */
  private RuntimeException launderThrowable(final Throwable cause) throws IOException {
    if (cause instanceof RuntimeException) {
      return (RuntimeException) cause;
    }
    if (cause instanceof IOException) {
      throw (IOException) cause;
    }
    return new IllegalStateException("could not execute helper: " + method.getName(), cause);
  }

  /**
   * Choose between context, options or a possible argument that matches the parameter type.
   *
   * @param paramType The expected parameter type.
   * @param argument The possible argument.
   * @param context The context object.
   * @param options The options object.
   * @return An object argument.
   */
  private Object argument(final Class<?> paramType, final Object argument, final Object context,
      final Options options) {
    // priority order is as follows:
    // 1. context
    // 2. argument
    // 3. options
    for (Object candidate : new Object[]{context, argument, options }) {
      if (paramType.isInstance(candidate) || wrap(paramType).isInstance(candidate)) {
        return candidate;
      }
    }
    return null;
  }

  /**
   * Wrap (if possible) a primitive type to their wrapper.
   *
   * @param type The candidate type.
   * @return A wrapper for the primitive type or the original type.
   */
  private static Class<?> wrap(final Class<?> type) {
    if (type.isPrimitive()) {
      if (type == Integer.TYPE) {
        return Integer.class;
      } else if (type == Boolean.TYPE) {
        return Boolean.class;
      } else if (type == Character.TYPE) {
        return Character.class;
      } else if (type == Double.TYPE) {
        return Double.class;
      } else if (type == Long.TYPE) {
        return Long.class;
      } else if (type == Float.TYPE) {
        return Float.class;
      } else if (type == Short.TYPE) {
        return Short.class;
      } else if (type == Byte.TYPE) {
        return Byte.class;
      }
    }
    return type;
  }
}
