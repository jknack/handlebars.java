package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Scopes;
import com.github.edgarespina.handlerbars.Template;

class Section extends Template {

  private static class LambdaScope {

    public final Lambda lambda;

    public final Scope scope;

    public LambdaScope(final Lambda lambda, final Scope parent) {
      this.lambda = lambda;
      this.scope = parent;
    }

  }

  private enum Transformer {
    NONE,

    LAMBDA {
      @Override
      protected boolean apply(final Object candidate) {
        return candidate instanceof Lambda;
      }

      @Override
      public Object transform(final Scope scope,
          final Object candidate) {
        return new LambdaScope((Lambda) candidate, scope);
      }
    },

    ARRAY {
      @Override
      protected boolean apply(final Object candidate) {
        return candidate != null && candidate.getClass().isArray();
      }

      @Override
      public Object transform(final Scope scope,
          final Object candidate) {
        int size = Array.getLength(candidate);
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < size; i++) {
          list.add(Array.get(candidate, i));
        }
        return list;
      }
    };

    protected boolean apply(final Object candidate) {
      return false;
    }

    public Object transform(final Scope scope,
        final Object candidate) {
      return candidate;
    }

    public static Transformer get(final Object candidate) {
      EnumSet<Transformer> transoformers = EnumSet.allOf(Transformer.class);
      transoformers.remove(NONE);
      for (Transformer transformer : transoformers) {
        if (transformer.apply(candidate)) {
          return transformer;
        }
      }
      return NONE;
    }
  }

  private enum Type {
    UNKNOWN {
      @Override
      public boolean apply(final Object candidate) {
        return true;
      }

      @Override
      public boolean traverse(final Object candidate) {
        throw new IllegalArgumentException("Value: '" + candidate
            + "' cannot be used in a section.");
      }

      @Override
      public void merge(final Writer writer, final Template body,
          final Scope scope, final String name, final Object candidate)
          throws IOException {
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
      public void merge(final Writer writer, final Template body,
          final Scope scope, final String name, final Object candidate)
          throws IOException {
        body.merge(scope, writer);
      }
    },

    NULL {
      @Override
      public boolean apply(final Object candidate) {
        return candidate == null;
      }

      @Override
      public boolean traverse(final Object candidate) {
        return false;
      }

      @Override
      public void merge(final Writer writer, final Template body,
          final Scope scope, final String name, final Object candidate)
          throws IOException {
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
      public void merge(final Writer writer, final Template body,
          final Scope scope, final String name, final Object candidate)
          throws IOException {
        body.merge(Scopes.scope(scope, candidate), writer);
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
      public void merge(final Writer writer, final Template body,
          final Scope scope, final String name, final Object candidate)
          throws IOException {
        Iterable<Object> elements = (Iterable<Object>) candidate;
        for (Object element : elements) {
          body.merge(Scopes.scope(scope, element), writer);
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
      public void merge(final Writer writer, final Template body,
          final Scope scope, final String name, final Object candidate)
          throws IOException {
        body.merge(scope, writer);
      }
    },

    LAMBDA {
      @Override
      public boolean apply(final Object candidate) {
        return candidate instanceof LambdaScope;
      }

      @Override
      public boolean traverse(final Object candidate) {
        return true;
      }

      @Override
      public void merge(final Writer writer, final Template body,
          final Scope scope, final String name, final Object candidate)
          throws IOException {
        throw new UnsupportedOperationException();
      }
    };

    public abstract boolean apply(Object candidate);

    public abstract boolean traverse(Object candidate);

    public abstract void merge(Writer writer, Template body,
        Scope scope, String name, Object candidate) throws IOException;

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

  private Template body;

  private final String name;

  private final boolean inverted;

  public Section(final String name, final boolean inverted) {
    this.name = name;
    this.inverted = inverted;
  }

  @Override
  public void merge(final Scope scope,
      final Writer writer) throws IOException {
    Object candidate = scope.get(name);
    candidate = Transformer.get(candidate).transform(scope, candidate);
    Type type = Type.get(candidate);
    boolean traverse = type.traverse(candidate);
    if (traverse) {
      type.merge(writer, body, scope, name, candidate);
    } else if (inverted) {
      Type.INVERTED.merge(writer, body, scope, name, candidate);
    }
  }

  @Override
  public String toString() {
    return "{{#" + name + "}}" + body.toString() + "{{/" + name + "}}";
  }

  public String name() {
    return name;
  }

  public boolean inverted() {
    return inverted;
  }

  public Section body(final Template body) {
    this.body = body;
    return this;
  }

  public Template body() {
    return body;
  }
}
