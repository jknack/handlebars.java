package com.github.edgarespina.handlerbars.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;


class ObjectScope extends BaseScope<Object> {

  private static interface MethodCallback {
    Object doWith(Method method) throws Exception;
  }

  private static interface MethodFilter {
    boolean matches(Method method);
  }

  public ObjectScope(final Scope parent, final Object self) {
    super(parent, self);
  }

  @Override
  protected Object get(final LinkedList<String> path) {
    Object current = context;
    for (int i = 0; i < path.size() - 1; i++) {
      current = invoke(current, path.get(i));
      if (current == null) {
        return null;
      }
    }
    String name = path.getLast();
    Object value = invoke(current, name);
    if (value == null && current != context) {
      // We're looking in the right scope, but the value isn't there
      // returns a custom mark to stop looking
      value = NULL;
    }
    return value;
  }

  private Object invoke(final Object current, final String property) {
    final String getMethod = javaBeanMethod("get", property);
    final String isMethod = javaBeanMethod("is", property);
    Object value = invoke(current.getClass(), new MethodCallback() {
      @Override
      public Object doWith(final Method method) throws Exception {
        return method.invoke(current);
      }
    }, new MethodFilter() {
      @Override
      public boolean matches(final Method method) {
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        return
        !isStatic &&
            (method.getName().equals(getMethod)
            || method.getName().equals(isMethod));
      }
    });
    return value;
  }

  private String javaBeanMethod(final String prefix, final String name) {
    StringBuilder buffer = new StringBuilder(prefix);
    buffer.append(name);
    buffer.setCharAt(prefix.length(), Character.toUpperCase(name.charAt(0)));
    return buffer.toString();
  }

  /**
   * Perform the given callback operation on all matching methods of the given
   * class and superclasses (or given interface and super-interfaces).
   * <p>
   * The same named method occurring on subclass and superclass will appear
   * twice, unless excluded by the specified {@link MethodFilter}.
   *
   * @param clazz class to start looking at
   * @param mc the callback to invoke for each method
   * @param mf the filter that determines the methods to apply the callback to
   */
  public static Object invoke(final Class<?> clazz,
      final MethodCallback mc, final MethodFilter mf)
      throws IllegalArgumentException {

    // Keep backing up the inheritance hierarchy.
    Method[] methods = clazz.getDeclaredMethods();
    for (Method method : methods) {
      if (mf != null && !mf.matches(method)) {
        continue;
      }
      try {
        return mc.doWith(method);
      } catch (Exception ex) {
        throw new IllegalStateException(
            "Shouldn't be illegal to access method '" + method.getName()
                + "': " + ex);
      }
    }
    if (clazz.getSuperclass() != null) {
      return invoke(clazz.getSuperclass(), mc, mf);
    }
    else if (clazz.isInterface()) {
      for (Class<?> superIfc : clazz.getInterfaces()) {
        return invoke(superIfc, mc, mf);
      }
    }
    return null;
  }

}
