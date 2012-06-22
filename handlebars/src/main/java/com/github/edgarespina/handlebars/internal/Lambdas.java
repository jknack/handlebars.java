package com.github.edgarespina.handlebars.internal;

import java.io.IOException;

import com.github.edgarespina.handlebars.Handlebars;
import com.github.edgarespina.handlebars.Lambda;
import com.github.edgarespina.handlebars.Template;
import com.github.edgarespina.handlebars.internal.Variable.Type;

/**
 * Utilities function for work with lambdas.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
final class Lambdas {

  /**
   * Not allowed.
   */
  private Lambdas() {
  }

  /**
   * Merge the lambda result.
   *
   * @param handlebars The handlebars.
   * @param lambda The lambda.
   * @param scope The current scope.
   * @param template The current template.
   * @return The resulting text.
   * @throws IOException If the resource cannot be loaded.
   */
  public static CharSequence merge(final Handlebars handlebars,
      final Lambda<Object, Object> lambda, final Context scope,
      final Template template) throws IOException {
    BaseTemplate result = compile(handlebars, lambda, scope, template);
    return result.apply(scope);
  }

  /**
   * Compile the given lambda.
   *
   * @param handlebars The handlebars.
   * @param lambda The lambda.
   * @param scope The current scope.
   * @param template The template.
   * @return The resulting template.
   * @throws IOException If the resource cannot be loaded.
   */
  public static BaseTemplate compile(final Handlebars handlebars,
      final Lambda<Object, Object> lambda, final Context scope,
      final Template template)
      throws IOException {
    return compile(handlebars, lambda, scope, template, "{{", "}}");
  }

  /**
   * Compile the given lambda.
   *
   * @param handlebars The handlebars.
   * @param lambda The lambda.
   * @param scope The current scope.
   * @param template The template.
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   * @return The resulting template.
   * @throws IOException If the resource cannot be loaded.
   */
  public static BaseTemplate compile(final Handlebars handlebars,
      final Lambda<Object, Object> lambda, final Context scope,
      final Template template, final String startDelimiter,
      final String endDelimiter)
      throws IOException {
    Object value = lambda.apply(scope, template);
    BaseTemplate result;
    if (value instanceof CharSequence) {
      result =
          (BaseTemplate) handlebars.compile(value.toString(), startDelimiter,
              endDelimiter);
    } else {
      // Don't escape no string values.
      result = new Variable(handlebars, "$$lambda", value, Type.TRIPLE_VAR);
    }
    return result;
  }
}
