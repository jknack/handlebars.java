/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

/**
 * When the value is a callable object, such as a lambda, the object will be invoked and passed the
 * block of text. The text passed is the literal block, unrendered. {{tags}} will not have been
 * expanded - the lambda should do that on its own. In this way you can implement filters or
 * caching.
 *
 * <p>Template:
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
 * hash.put("wrapped", new Lambda&lt;String&gt;() {
 *   public String apply(Scope scope, Template template) {
 *    return "<b>" + template.apply(scope) + "</b>";
 *   }
 * });
 * </pre>
 *
 * <p>Output:
 *
 * <pre>
 * <b>Willy is awesome.</b>
 * </pre>
 *
 * @author edgar.espina
 * @param <C> The lambda context.
 * @param <O> The lambda output.
 */
public interface Lambda<C, O> {

  /**
   * Apply the lambda.
   *
   * @param context The current context.
   * @param template The current template.
   * @return The resulting text.
   * @throws IOException If the resource cannot be loaded.
   */
  O apply(C context, Template template) throws IOException;
}
