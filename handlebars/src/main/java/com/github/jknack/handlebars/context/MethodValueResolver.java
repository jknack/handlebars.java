/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.jknack.handlebars.ValueResolver;

/**
 * A specialization of {@link MemberValueResolver} with lookup and invocation support for {@link
 * Method}. It matches a public method.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public class MethodValueResolver extends MemberValueResolver<Method> {

  /** The default instance. */
  public static final ValueResolver INSTANCE = new MethodValueResolver();

  /** Args for getters. */
  private static final Object[] EMPTY_ARGS = new Object[0];

  @Override
  public boolean matches(final Method method, final String name) {
    int parameterCount = method.getParameterTypes().length;
    return isPublic(method) && method.getName().equals(name) && parameterCount == 0;
  }

  @Override
  protected Object invokeMember(final Method member, final Object context) {
    try {
      return member.invoke(context, EMPTY_ARGS);
    } catch (InvocationTargetException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new IllegalStateException("Execution of '" + member.getName() + "' failed", cause);
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException("Could not access method:  '" + member.getName() + "'", ex);
    }
  }

  @Override
  protected Set<Method> members(final Class<?> clazz) {
    Set<Method> members = new LinkedHashSet<>();
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
      }
      for (Class<?> superIfc : clazz.getInterfaces()) {
        members(superIfc, members);
      }
    }
  }

  @Override
  protected String memberName(final Method member) {
    return member.getName();
  }
}
