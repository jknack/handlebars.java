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
    if (args.length > 0) {
      // one arg helper must be: Context or Options
      if (args.length == 1) {
        if (paramTypes[0] == Options.class) {
          args[0] = options;
        } else {
          args[0] = context;
        }
      } else {
        // multi arg helper: 1st arg must be context, then args and may be options
        args[0] = context;
        for (int i = 0; i < options.params.length; i++) {
          args[i + 1] = options.param(i);
        }
        if (args.length > options.params.length + 1) {
          args[args.length - 1] = options;
        }
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

}
