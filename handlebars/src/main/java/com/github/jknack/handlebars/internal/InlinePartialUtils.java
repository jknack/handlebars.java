/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.github.jknack.handlebars.Template;

/** Utilities for resolving inline partials across nested scopes. */
public final class InlinePartialUtils {

  private InlinePartialUtils() {}

  /**
   * Finds the nearest inline partial with the given name.
   *
   * @param partials Inline partial scopes.
   * @param name Inline partial name.
   * @return The nearest inline partial, or null if none is defined.
   */
  public static Template find(final LinkedList<Map<String, Template>> partials, final String name) {
    Iterator<Map<String, Template>> scopes = partials.descendingIterator();
    while (scopes.hasNext()) {
      Map<String, Template> scope = scopes.next();
      if (scope.containsKey(name)) {
        return scope.get(name);
      }
    }
    return null;
  }

  /**
   * Finds the nearest partial block in the inline partial scope stack.
   *
   * @param partials Inline partial scopes.
   * @return The nearest partial block, or null if none is defined.
   */
  public static Template partialBlock(final LinkedList<Map<String, Template>> partials) {
    return find(partials, "@partial-block");
  }
}
