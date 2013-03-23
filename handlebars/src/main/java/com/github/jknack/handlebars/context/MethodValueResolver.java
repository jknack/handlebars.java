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
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.jknack.handlebars.ValueResolver;

/**
 * A specialization of {@link MemberValueResolver} with lookup and invocation
 * support for {@link Method}.
 * It matches a public method.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public class MethodValueResolver extends MemberValueResolver<Method> {

  /**
   * The default instance.
   */
  public static final ValueResolver INSTANCE = new MethodValueResolver();

  @Override
  public boolean matches(final Method method, final String name) {
    return isPublic(method) && method.getName().equals(name);
  }

  @Override
  protected Object invokeMember(final Method member, final Object context) {
    try {
      return member.invoke(context);
    } catch (Exception ex) {
      throw new IllegalStateException(
          "Shouldn't be illegal to access method '" + member.getName()
              + "'", ex);
    }
  }

  @Override
  protected Set<Method> members(final Class<?> clazz) {
    Set<Method> members = new LinkedHashSet<Method>();
    members(clazz, members);
    return members;
  }

  /**
   * Collect all the method from the given class.
   *
   * @param clazz The base class.
   * @param members The members result set.
   */
  protected void members(final Class<?> clazz, final Set<Method> members) {
    if (clazz != Object.class) {
      // Keep backing up the inheritance hierarchy.
      Method[] methods = clazz.getDeclaredMethods();
      for (Method method : methods) {
        if (matches(method, memberName(method))) {
          members.add(method);
        }
      }
      if (clazz.getSuperclass() != null) {
        members(clazz.getSuperclass(), members);
      } else if (clazz.isInterface()) {
        for (Class<?> superIfc : clazz.getInterfaces()) {
          members(superIfc, members);
        }
      }
    }
  }

  @Override
  protected String memberName(final Method member) {
    return member.getName();
  }
}
