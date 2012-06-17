package com.github.edgarespina.handlerbars.internal;

import java.util.LinkedList;
import java.util.Map;


class MapScope extends BaseScope<Map<String, Object>> {

  public MapScope(final Scope parent, final Map<String, Object> self) {
    super(parent, self);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Object get(final LinkedList<String> path) {
    Map<String, Object> current = context;
    for (int i = 0; i < path.size() - 1; i++) {
      current = (Map<String, Object>) current.get(path.get(i));
      if (current == null) {
        return null;
      }
    }
    String name = path.getLast();
    Object value = current.get(name);
    if (value == null && current != context) {
      // We're looking in the right scope, but the value isn't there
      // returns a custom mark to stop looking
      value = NULL;
    }
    return value;
  }

}
