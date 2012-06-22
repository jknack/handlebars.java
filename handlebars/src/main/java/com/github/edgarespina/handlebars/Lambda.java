package com.github.edgarespina.handlebars;

import java.io.IOException;

/**
 * <p>
 * When the value is a callable object, such as a lambda, the object will be
 * invoked and passed the block of text. The text passed is the literal block,
 * unrendered. {{tags}} will not have been expanded - the lambda should do that
 * on its own. In this way you can implement filters or caching.
 * </p>
 * <p>
 * Template:
 * </p>
 *
 * <pre>
 * {{#wrapped}}
 * {{name}} is awesome.
 * {{/wrapped}}
 * </pre>
 *
 * Hash:
 *
 * <pre>
 * Map hash = ...
 * hash.put("name", "Willy");
 * hash.put("wrapped", new Lambda<String>() {
 *   public String apply(Scope scope, Template template) {
 *    return "<b>" + template.apply(scope) + "</b>";
 *   }
 * });
 * </pre>
 * <p>
 * Output:
 * </p>
 *
 * <pre>
 * <b>Willy is awesome.</b>
 * </pre>
 *
 * @author edgar.espina
 * @param <Context> The lambda context.
 * @param <Out> The lambda output.
 */
public interface Lambda<Context, Out> {

  /**
   * Apply the lambda.
   *
   * @param context The current context.
   * @param template The current template.
   * @return The resulting text.
   * @throws IOException If the resource cannot be loaded.
   */
  Out apply(Context context, Template template) throws IOException;
}
