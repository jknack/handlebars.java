package com.github.edgarespina.handlebars;

/**
 * A hook interface for resolving values from the {@link Context context stack}.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public interface ValueResolver {

  /**
   * A mark object.
   */
  Object UNRESOLVED = new Object();

  /**
   * Resolve the attribute's name in the context object. If a
   * {@link #UNRESOLVED} is returned, the {@link Context context stack} will
   * continue with the next value resolver in the chain.
   *
   * @param context The context object. Not null.
   * @param name The attribute's name. Not null.
   * @return A {@link #UNRESOLVED} is returned, the {@link Context context
   *         stack} will continue with the next value resolver in the chain.
   *         Otherwise, it returns the associated value.
   */
  Object resolve(Object context, String name);
}
