package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.io.Writer;

public interface Template {

  void merge(Scope scope, Writer writer) throws IOException;

  String merge(final Scope scope) throws IOException;

  @Override
  public abstract String toString();
}
