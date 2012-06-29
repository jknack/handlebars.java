package com.github.edgarespina.handlebars.context;

import java.lang.reflect.Method;

import com.github.edgarespina.handlebars.ValueResolver;

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
  protected Method findMember(final Class<?> clazz, final String name) {
    // Keep backing up the inheritance hierarchy.
    Method[] methods = clazz.getDeclaredMethods();
    for (Method method : methods) {
      if (matches(method, name)) {
        return method;
      }
    }
    if (clazz.getSuperclass() != null) {
      return findMember(clazz.getSuperclass(), name);
    } else if (clazz.isInterface()) {
      for (Class<?> superIfc : clazz.getInterfaces()) {
        return findMember(superIfc, name);
      }
    }
    return null;
  }

}
