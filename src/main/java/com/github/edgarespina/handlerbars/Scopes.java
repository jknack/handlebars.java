package com.github.edgarespina.handlerbars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Scopes {

  private static class ForwardingMap implements Scope {

    private Map<String, Object> delegate;

    private final LinkedList<String> root = new LinkedList<String>();

    public ForwardingMap(final Map<String, Object> delegate) {
      this.delegate = delegate;
    }

    @Override
    public int size() {
      return delegate.size();
    }

    @Override
    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
      return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
      return delegate.containsValue(value);
    }

    @Override
    public Object get(final Object key) {
      // Hierarchical
      List<String> tail = split(key);
      Map<String, Object> scope = scope(qualify(tail));
      if (scope == null) {
        // Plain
        scope = scope(tail);
      }
      if (scope == null) {
        return null;
      }
      String name = tail.get(tail.size() - 1);
      Object value = scope.get(name);
      if (value == null) {
        // Plain
        scope = scope(tail);
        if (tail.size() == 1) {
          value = scope;
        } else if (scope != null) {
          value = scope.get(name);
        }
      }
      return value;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> scope(final List<String> fullpath) {
      Map<String, Object> scope = delegate;
      for (int i = 0; i < fullpath.size() - 1; i++) {
        scope = (Map<String, Object>) scope.get(fullpath.get(i));
        if (scope == null) {
          break;
        }
      }
      return scope;
    }

    @Override
    public Scope push(final String name) {
      root.addLast(name);
      return this;
    }

    @Override
    public Scope pop() {
      root.removeLast();
      return this;
    }

    private List<String> split(final Object key) {
      StringTokenizer tokenizer = new StringTokenizer(key.toString(), ".");
      List<String> path = new ArrayList<String>(tokenizer.countTokens());
      while (tokenizer.hasMoreTokens()) {
        path.add(tokenizer.nextToken());
      }
      return path;
    }

    private List<String> qualify(final List<String> tail) {
      if (root.size() == 0) {
        return tail;
      }
      List<String> path = new ArrayList<String>(root.size() + tail.size());
      path.addAll(root);
      path.addAll(tail);
      return path;
    }

    @Override
    public Object put(final String key, final Object value) {
      return delegate.put(key, value);
    }

    @Override
    public Object remove(final Object key) {
      return delegate.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> map) {
      delegate.putAll(map);
    }

    @Override
    public void clear() {
      delegate.clear();
    }

    @Override
    public Set<String> keySet() {
      return delegate.keySet();
    }

    @Override
    public Collection<Object> values() {
      return delegate.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
      return delegate.entrySet();
    }

    @Override
    public String toString() {
      return delegate.toString();
    }
  }

  public static Scope scope(final Map<String, Object> model) {
    return new ForwardingMap(model);
  }

  public static Scope newScope() {
    return scope(new HashMap<String, Object>());
  }

}
