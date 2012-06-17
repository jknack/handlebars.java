package com.github.edgarespina.handlerbars.internal;

import java.util.Map;


public class Scopes {

  public static Scope scope(final Object candidate) {
    return scope(null, candidate);
  }

  @SuppressWarnings({"unchecked" })
  public static Scope scope(final Scope parent, final Object candidate) {
    if (candidate == null) {
      return Scope.NONE;
    }
    if (candidate instanceof Map) {
      return new MapScope(parent, (Map<String, Object>) candidate);
    }
    if (candidate instanceof Scope) {
      return (Scope) candidate;
    }
    return new ObjectScope(parent, candidate);
  }

}
