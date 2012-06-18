package com.github.edgarespina.handlerbars.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

class Context {
  private static interface MethodCallback {
    Object doWith(Method method) throws Exception;
  }

  private static interface MethodFilter {
    boolean matches(Method method);
  }

  static final Object NULL = new Object();

  private static final Context NONE = new Context(null, null) {
    @Override
    public Object get(final Object key) {
      return null;
    }
  };

  private Context parent;

  protected final Object target;

  public Context(final Context parent, final Object target) {
    this.parent = parent;
    this.target = target;
  }

  public Object target() {
    return target;
  }

  public Object get(final Object key) {
    if (".".equals(key) || "this".equals(key)) {
      return target;
    }
    // 1. Objects and hashes should be pushed onto the context stack.
    // 2. All elements on the context stack should be accessible.
    // 3. Multiple sections per template should be permitted.
    // 4. Failed context lookups should be considered falsey.
    // 5. Dotted names should be valid for Section tags.
    // 6. Dotted names that cannot be resolved should be considered falsey.
    // 7. Dotted Names - Context Precedence: Dotted names should be resolved
    // against former resolutions.
    LinkedList<String> path = path(key);
    Object value = get(path);
    if (value == null && parent != null) {
      value = parent.get(key);
    }
    return value == NULL ? null : value;
  }

  private LinkedList<String> path(final Object key) {
    LinkedList<String> path = new LinkedList<String>();
    StringTokenizer tokenizer = new StringTokenizer(key.toString(), ".");
    while (tokenizer.hasMoreTokens()) {
      path.add(tokenizer.nextToken());
    }
    return path;
  }

  private Object get(final LinkedList<String> path) {
    Object current = target;
    for (int i = 0; i < path.size() - 1; i++) {
      current = get(current, path.get(i));
      if (current == null) {
        return null;
      }
    }
    String name = path.getLast();
    Object value = get(current, name);
    if (value == null && current != target) {
      // We're looking in the right scope, but the value isn't there
      // returns a custom mark to stop looking
      value = NULL;
    }
    return value;
  }

  @SuppressWarnings("rawtypes")
  protected Object get(final Object current, final String name) {
    final Object value;
    if (current instanceof Map) {
      value = ((Map) current).get(name);
    } else {
      value = invoke(current, name);
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

  @Override
  public String toString() {
    return target.toString();
  }

  public static Context scope(final Object candidate) {
    return scope(null, candidate);
  }

  public static Context scope(final Context parent, final Object candidate) {
    if (candidate == null) {
      return Context.NONE;
    }
    if (candidate instanceof Context) {
      return (Context) candidate;
    }
    return new Context(parent, candidate);
  }
}
