package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Template;

class Section extends Template {

  private static class LambdaScope {

    public final Lambda lambda;

    public final Map<String, Object> scope;

    public LambdaScope(final Lambda lambda,
        final Map<String, Object> parent) {
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
      public Object transform(final Map<String, Object> scope,
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
      public Object transform(final Map<String, Object> scope,
          final Object candidate) {
        int size = Array.getLength(candidate);
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < size; i ++) {
          list.add(Array.get(candidate, i));
        }
        return list;
      }
    };

    protected boolean apply(final Object candidate) {
      return false;
    }

    public Object transform(final Map<String, Object> scope,
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
        throw new IllegalArgumentException("Value: " + candidate
            + " cannot be used in a section.");
      }

      @Override
      public void merge(final Writer writer, final Template body,
          final Object candidate) throws IOException {
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
          final Object candidate) throws IOException {
        body.merge(wrap(candidate), writer);
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
          final Object candidate) throws IOException {
        body.merge(wrap(candidate), writer);
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
          final Object candidate) throws IOException {
        Collection<Object> elements = (Collection<Object>) candidate;
        for (Object object : elements) {
          body.merge(wrap(object), writer);
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
          final Object candidate) throws IOException {
        body.merge(wrap(candidate), writer);
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
          final Object candidate) throws IOException {
        LambdaScope lambdaScope = (LambdaScope) candidate;
        String result = lambdaScope.lambda.apply(body, lambdaScope.scope);
        writer.append(result);
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

    public abstract void merge(Writer writer, Template body,
        Object candidate) throws IOException;

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
  public void merge(final Map<String, Object> scope,
      final Writer writer) throws IOException {
    Object candidate = scope.get(name);
    candidate = Transformer.get(candidate).transform(scope, candidate);
    Type type = Type.get(candidate);
    boolean traverse = type.traverse(candidate);
    if (traverse) {
      type.merge(writer, body, candidate);
    } else if (inverted) {
      Type.INVERTED.merge(writer, body, candidate);
    }
  }

  @Override
  public String toString() {
    return body.toString();
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
