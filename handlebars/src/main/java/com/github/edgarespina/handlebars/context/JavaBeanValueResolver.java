package com.github.edgarespina.handlebars.context;

import java.lang.reflect.Method;

import com.github.edgarespina.handlebars.ValueResolver;

/**
 * A JavaBean method value resolver.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public class JavaBeanValueResolver extends MethodValueResolver {

  /**
   * The default value resolver.
   */
  public static final ValueResolver INSTANCE = new JavaBeanValueResolver();

  @Override
  public boolean matches(final Method method, final String name) {
    return !isStatic(method) && isPublic(method)
        && (method.getName().equals(javaBeanMethod("get", name))
        || method.getName().equals(javaBeanMethod("is", name)));
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
}
