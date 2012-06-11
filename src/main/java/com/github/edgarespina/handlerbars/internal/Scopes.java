package com.github.edgarespina.handlerbars.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.github.edgarespina.handlerbars.Scope;

public class Scopes {

  private static class ForwardingMap implements Scope {

    private static final Object NULL = new Object();

    private Map<String, Object> scope;

    private Scope parent;

    public ForwardingMap(final Scope parent, final Map<String, Object> model) {
      this.parent = parent;
      this.scope = model;
    }

    @Override
    public Object get(final Object key) {
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
      while (value == null && parent != null) {
        value = parent.get(key);
      }
      return value == NULL ? null : value;
    }

    private LinkedList<String> path(final Object key) {
      LinkedList<String> path = new LinkedList<String>();
      if (key.equals(".")) {
        path.add(".");
      } else {
        StringTokenizer tokenizer = new StringTokenizer(key.toString(), ".");
        while (tokenizer.hasMoreTokens()) {
          path.add(tokenizer.nextToken());
        }
      }
      return path;
    }

    @SuppressWarnings("unchecked")
    private Object get(final LinkedList<String> path) {
      Map<String, Object> current = scope;
      for (int i = 0; i < path.size() - 1; i++) {
        current = (Map<String, Object>) current.get(path.get(i));
        if (current == null) {
          return null;
        }
      }
      String name = path.getLast();
      Object value = current.get(name);
      if (value == null && current != scope) {
        // We're looking in the right scope, but the value isn't there
        // returns a custom mark to stop looking
        value = NULL;
      }
      return value;
    }

    @Override
    public int size() {
      return scope.size();
    }

    @Override
    public boolean isEmpty() {
      return scope.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
      return scope.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
      return scope.containsValue(value);
    }

    @Override
    public Object put(final String key, final Object value) {
      return scope.put(key, value);
    }

    @Override
    public Object remove(final Object key) {
      return scope.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> map) {
      scope.putAll(map);
    }

    @Override
    public void clear() {
      scope.clear();
    }

    @Override
    public Set<String> keySet() {
      return scope.keySet();
    }

    @Override
    public Collection<Object> values() {
      return scope.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
      return scope.entrySet();
    }

    @Override
    public String toString() {
      return scope.toString();
    }
  }

  public static Scope scope(final Map<String, Object> model) {
    return new ForwardingMap(null, model);
  }

  @SuppressWarnings({"unchecked" })
  public static Scope scope(final Scope parent, final Object data) {
    final Map<String, Object> scope;
    if (data instanceof Map) {
      scope = (Map<String, Object>) data;
    } else {
      scope = new HashMap<String, Object>();
      scope.put(".", data);
    }
    return new ForwardingMap(parent, scope);
  }

  public static Scope newScope() {
    return scope(new HashMap<String, Object>());
  }

}
