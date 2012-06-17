package com.github.edgarespina.handlerbars.internal;

import java.util.LinkedList;
import java.util.StringTokenizer;


abstract class BaseScope<T> implements Scope {

  static final Object NULL = new Object();

  private Scope parent;

  protected final T context;

  public BaseScope(final Scope parent, final T context) {
    this.parent = parent;
    this.context = context;
  }

  @Override
  public Object context() {
    return context;
  }

  @Override
  public final Object get(final Object key) {
    if (".".equals(key) || "this".equals(key)) {
      return context;
    }
    // 1. Objects and hashes should be pushed onto the context stack.
    // 2. All elements on the context stack should be accessible.
    // 3. Multiple sections per template should be permitted.
    // 4. Failed context lookups should be considered falsey.
    // 5. Dotted names should be valid for Section tags.
    // 6. Dotted names that cannot be resolved should be considered falsey.
    // 7. Dotted Names - Context Precedence: Dotted names should be resolved
    // against former resolutions.
    LinkedList<String> path = path(key);
    Object value = get(path);
    if (value == null && parent != null) {
      value = parent.get(key);
    }
    return value == NULL ? null : value;
  }

  private LinkedList<String> path(final Object key) {
    LinkedList<String> path = new LinkedList<String>();
    StringTokenizer tokenizer = new StringTokenizer(key.toString(), ".");
    while (tokenizer.hasMoreTokens()) {
      path.add(tokenizer.nextToken());
    }
    return path;
  }

  protected abstract Object get(final LinkedList<String> path);

  @Override
  public String toString() {
    return context.toString();
  }
}
