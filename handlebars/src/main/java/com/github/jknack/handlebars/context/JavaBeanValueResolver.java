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
package com.github.jknack.handlebars.context;

import java.lang.reflect.Method;

import com.github.jknack.handlebars.ValueResolver;

/**
 * A JavaBean method value resolver.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public class JavaBeanValueResolver extends MethodValueResolver {

  /**
   * The 'is' prefix.
   */
  private static final String IS_PREFIX = "is";

  /**
   * The 'get' prefix.
   */
  private static final String GET_PREFIX = "get";

  /**
   * The default value resolver.
   */
  public static final ValueResolver INSTANCE = new JavaBeanValueResolver();

  @Override
  public boolean matches(final Method method, final String name) {
    boolean isStatic = isStatic(method);
    boolean isPublic = isPublic(method);
    boolean isGet = method.getName().equals(javaBeanMethod(GET_PREFIX, name));
    boolean isBoolGet = method.getName().equals(javaBeanMethod(IS_PREFIX, name));
    int parameterCount = method.getParameterTypes().length;

    return !isStatic && isPublic && parameterCount == 0 && (isGet || isBoolGet);
  }

  /**
   * Convert the property's name to a JavaBean read method name.
   *
   * @param prefix The prefix: 'get' or 'is'.
   * @param name The unqualified property name.
   * @return The javaBean method name.
   */
  private static String javaBeanMethod(final String prefix,
      final String name) {
    StringBuilder buffer = new StringBuilder(prefix);
    buffer.append(name);
    buffer.setCharAt(prefix.length(), Character.toUpperCase(name.charAt(0)));
    return buffer.toString();
  }

  @Override
  protected String memberName(final Method member) {
    String name = member.getName();
    if (name.startsWith(GET_PREFIX)) {
      name = name.substring(GET_PREFIX.length());
    } else if (name.startsWith(IS_PREFIX)) {
      name = name.substring(IS_PREFIX.length());
    } else {
      return name;
    }
    if (name.length() > 0) {
      return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
    return member.getName();
  }
}
