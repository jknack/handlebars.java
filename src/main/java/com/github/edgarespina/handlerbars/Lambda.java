package com.github.edgarespina.handlerbars;

import java.io.IOException;

import com.github.edgarespina.handlerbars.internal.Scope;

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
 * Hash:
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
 * @param <Out> The lambda output.
 */
public interface Lambda<Out> {

  Out apply(Scope scope, Template template) throws IOException;
}
