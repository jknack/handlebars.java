package com.github.edgarespina.handlebars.context;

import java.lang.reflect.Field;

import com.github.edgarespina.handlebars.ValueResolver;

/**
 * A specialization of {@link MemberValueResolver} with lookup and invocation
 * support for {@link Field}.
 * It matches private, protected, package, public and no-static field.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public class FieldValueResolver extends MemberValueResolver<Field> {

  /**
   * The default value resolver.
   */
  public static final ValueResolver INSTANCE = new FieldValueResolver();

  @Override
  public boolean matches(final Field field, final String name) {
    return !isStatic(field) && field.getName().equals(name);
  }

  @Override
  protected Object invokeMember(final Field field, final Object context) {
    try {
      return field.get(context);
    } catch (Exception ex) {
      throw new IllegalStateException(
          "Shouldn't be illegal to access field '" + field.getName()
              + "'", ex);
    }
  }

  @Override
  protected Field findMember(final Class<?> clazz, final String name) {
    Class<?> targetClass = clazz;
    do {
      Field[] fields = targetClass.getDeclaredFields();
      for (Field field : fields) {
        if (matches(field, name)) {
          return field;
        }
      }
      targetClass = targetClass.getSuperclass();
    } while (targetClass != null && targetClass != Object.class);
    return null;
  }

}
