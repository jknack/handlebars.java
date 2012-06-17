package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Template;

class LambdaWrapper implements DelimAware {

  private final Lambda<Object> lambda;

  private Handlebars handlebars;

  private String delimStart;

  private String delimEnd;

  public LambdaWrapper(final Handlebars handlebars,
      final Lambda<Object> lambda) {
    this.lambda = lambda;
    this.handlebars = handlebars;
  }

  public BaseTemplate apply(final Scope scope, final Template template)
      throws IOException {
    return Lambdas.compile(handlebars, lambda, scope, template, delimStart,
        delimEnd);
  }

  @Override
  public void setDelimiters(final String delimStart, final String delimEnd) {
    this.delimStart = delimStart;
    this.delimEnd = delimEnd;
  }

}
