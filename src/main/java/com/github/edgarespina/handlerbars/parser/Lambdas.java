package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Template;

public final class Lambdas {

  public static String merge(final Handlebars handlebars,
      final Lambda<Object> lambda,
      final Scope scope, final Template template) throws IOException {
    BaseTemplate result = compile(handlebars, lambda, scope, template);
    return result.merge(scope);
  }

  public static BaseTemplate compile(final Handlebars handlebars,
      final Lambda<Object> lambda, final Scope scope, final Template template)
      throws IOException {
    return compile(handlebars, lambda, scope, template, Handlebars.DELIM_START,
        Handlebars.DELIM_END);
  }

  public static BaseTemplate compile(final Handlebars handlebars,
      final Lambda<Object> lambda, final Scope scope, final Template template,
      final String delimStart, final String delimEnd)
      throws IOException {
    Object value = lambda.apply(scope, template);
    BaseTemplate result;
    if (value instanceof CharSequence) {
      result =
          (BaseTemplate) handlebars.compile(value.toString(), delimStart,
              delimEnd);
    } else {
      result = new Variable(handlebars, "$$lambda", value, false);
    }
    return result;
  }
}
