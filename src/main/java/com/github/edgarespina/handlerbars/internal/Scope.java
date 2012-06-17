package com.github.edgarespina.handlerbars.internal;


public interface Scope {
  Scope NONE = new Scope() {

    @Override
    public Object get(final Object name) {
      return null;
    }

    @Override
    public Object context() {
      return null;
    }
  };

  Object get(Object name);

  Object context();
}
