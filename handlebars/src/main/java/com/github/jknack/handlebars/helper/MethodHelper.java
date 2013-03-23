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
package com.github.jknack.handlebars.helper;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    Class<?>[] parameterTypes = method.getParameterTypes();
    List<Object> args = new ArrayList<Object>();
    // collect the parameters
    int pidx = 0;
    for (int i = 0; i < parameterTypes.length; i++) {
      Class<?> paramType = parameterTypes[i];
      Object arg = null;
      if (i == 0) {
        // param=0 might be the context it self
        if (paramType.isInstance(context)) {
          arg = context;
        }
      }
      if (arg == null && context != null) {
        arg = options.param(pidx, null);
        if (arg == null) {
          if (paramType.isInstance(options)) {
            arg = options;
          }
        } else {
          isTrue(paramType.isInstance(arg) || paramType.isAssignableFrom(unwrap(arg.getClass())),
              "found '%s', expected '%s'", arg.getClass().getName(), paramType.getName());
          pidx += 1;
        }
      }
      args.add(arg);
    }
    try {
      return (CharSequence) method.invoke(source, args.toArray(new Object[args.size()]));
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      if (ex instanceof IOException) {
        throw (IOException) ex;
      }
      throw new IllegalStateException("could not execute helper: " + method.getName(), ex);
    }
  }

  /**
   * Try to unwrapp a primitive wrap to his primitive class.
   *
   * @param clazz The candidate class.
   * @return unwrapp a primitive wrap to his primitive class. Or, return the same class for none
   *         primitive wrapper.
   */
  private static Class<?> unwrap(final Class<?> clazz) {
    if (clazz == Integer.class) {
      return Integer.TYPE;
    }
    if (clazz == Boolean.class) {
      return Boolean.TYPE;
    }
    if (clazz == Long.class) {
      return Long.TYPE;
    }
    if (clazz == Double.class) {
      return Double.TYPE;
    }
    if (clazz == Float.class) {
      return Float.TYPE;
    }
    if (clazz == Character.class) {
      return Character.TYPE;
    }
    if (clazz == Byte.class) {
      return Byte.TYPE;
    }
    if (clazz == Short.class) {
      return Short.TYPE;
    }
    return clazz;
  }
}
