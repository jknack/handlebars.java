package com.github.edgarespina.handlerbars.parser;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Section extends Node {

  private enum Type {
    UNKNOWN {
      @Override
      public boolean apply(final Object candidate) {
        return true;
      }

      @Override
      public boolean traverse(final Object candidate) {
        throw new IllegalArgumentException("Value: " + candidate
            + " cannot be used in a section.");
      }

      @Override
      public void toString(final StringBuilder output, final Node body,
          final Object candidate) {
      }
    },

    INVERTED {
      @Override
      public boolean apply(final Object candidate) {
        return true;
      }

      @Override
      public boolean traverse(final Object candidate) {
        return true;
      }

      @Override
      public void toString(final StringBuilder output, final Node body,
          final Object candidate) {
        body.toString(output, wrap(candidate));
      }
    },

    MAP {
      @Override
      public boolean apply(final Object candidate) {
        return candidate instanceof Map;
      }

      @SuppressWarnings("rawtypes")
      @Override
      public boolean traverse(final Object candidate) {
        return !((Map) candidate).isEmpty();
      }

      @Override
      public void toString(final StringBuilder output, final Node body,
          final Object candidate) {
        body.toString(output, wrap(candidate));
      }
    },

    COLLECTION {
      @Override
      public boolean apply(final Object candidate) {
        return candidate instanceof Collection;
      }

      @SuppressWarnings("rawtypes")
      @Override
      public boolean traverse(final Object candidate) {
        return !((Collection) candidate).isEmpty();
      }

      @SuppressWarnings("unchecked")
      @Override
      public void toString(final StringBuilder output, final Node body,
          final Object candidate) {
        Collection<Object> elements = (Collection<Object>) candidate;
        for (Object object : elements) {
          body.toString(output, wrap(object));
        }
      }
    },

    BOOLEAN {
      @Override
      public boolean apply(final Object candidate) {
        return candidate instanceof Boolean;
      }

      @Override
      public boolean traverse(final Object candidate) {
        return ((Boolean) candidate).booleanValue();
      }

      @Override
      public void toString(final StringBuilder output, final Node body,
          final Object candidate) {
        body.toString(output, wrap(candidate));
      }
    };

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Map<String, Object> wrap(final Object candidate) {
      Map<String, Object> scope = new HashMap<String, Object>();
      if (candidate instanceof Map) {
        scope.putAll((Map) candidate);
      }
      scope.put(".", candidate);
      return scope;
    }

    public abstract boolean apply(Object candidate);

    public abstract boolean traverse(Object candidate);

    public abstract void toString(StringBuilder output, Node body,
        Object candidate);

    public static Type get(final Object candidate) {
      EnumSet<Type> types = EnumSet.allOf(Type.class);
      types.remove(UNKNOWN);
      types.remove(INVERTED);
      for (Type type : types) {
        if (type.apply(candidate)) {
          return type;
        }
      }
      return UNKNOWN;
    }
  }

  private Node body;

  private String name;

  private boolean inverted;

  public Section() {
  }

  @Override
  public void toString(final StringBuilder output,
      final Map<String, Object> scope) {
    Object candidate = scope.get(name);
    Type type = Type.get(candidate);
    boolean traverse = type.traverse(candidate);
    if (traverse) {
      type.toString(output, body, candidate);
    } else if (inverted) {
      Type.INVERTED.toString(output, body, candidate);
    }

  }

  public String name() {
    return name;
  }

  public void name(final String name) {
    this.name = name;
  }

  public boolean inverted() {
    return inverted;
  }

  public void inverted(final boolean inverted) {
    this.inverted = inverted;
  }

  public Section body(final Node body) {
    this.body = body;
    return this;
  }

  public Node body() {
    return body;
  }
}
