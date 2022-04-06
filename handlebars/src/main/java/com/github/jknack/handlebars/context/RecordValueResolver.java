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

  public static RecordValueResolver INSTANCE = new RecordValueResolver();
  
  @Override
  protected boolean matches(Object context) {
    Class<?> superClass = context.getClass().getSuperclass();
    return superClass.getName().equals("java.lang.Record");
  }

}
