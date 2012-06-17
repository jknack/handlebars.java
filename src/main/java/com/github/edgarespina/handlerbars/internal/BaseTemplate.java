package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Template;

public abstract class BaseTemplate implements Template {

  public abstract boolean remove(Template child);

  @Override
  public String apply(final Object scope) throws IOException {
    return apply(Scopes.scope(scope));
  }

  @Override
  public void apply(final Object scope, final Writer writer)
      throws IOException {
    apply(Scopes.scope(scope), writer);
  }

  public final String apply(final Scope scope) throws IOException {
    FastStringWriter writer = new FastStringWriter();
    apply(scope, writer);
    return writer.toString();
  }

  public abstract void apply(final Scope scope, Writer writer)
      throws IOException;

  @Override
  public final String toString() {
    return text();
  }
}
