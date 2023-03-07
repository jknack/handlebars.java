package com.github.jknack.handlebars.context;

/**
 * A resolver for Record types.
 *
 * This resolver is safe to use in JDKs that do not support records yet.
 *
 * @author agentgt
 *
 */
public class RecordValueResolver extends MethodValueResolver {

  /**
   * The default instance.
   */
  public static final RecordValueResolver INSTANCE = new RecordValueResolver();

  @Override
  protected boolean matches(final Object context) {
    Class<?> superClass = context.getClass().getSuperclass();
    return superClass != null && superClass.getName().equals("java.lang.Record");
  }

}
