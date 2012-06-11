package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Template;

class Section extends BaseTemplate {

  private interface DelimAware {
    void setDelimiters(String delimStart, String delimEnd);
  }

  private static class SectionLambda implements DelimAware {

    public final Lambda lambda;

    private Handlebars handlebars;

    private String delimStart;

    private String delimEnd;

    public SectionLambda(final Handlebars handlebars, final Lambda lambda) {
      this.lambda = lambda;
      this.handlebars = handlebars;
    }

    public BaseTemplate apply(final Scope scope, final Template template)
        throws IOException {
      return Lambdas.compile(handlebars, lambda, scope, template, delimStart,
          delimEnd);
    }

    @Override
    public void setDelimiters(final String delimStart, final String delimEnd) {
      this.delimStart = delimStart;
      this.delimEnd = delimEnd;
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
      public Object transform(final Handlebars handlebars, final Scope scope,
          final Object candidate) {
        return new SectionLambda(handlebars, (Lambda) candidate);
      }
    },

    ARRAY {
      @Override
      protected boolean apply(final Object candidate) {
        return candidate != null && candidate.getClass().isArray();
      }

      @Override
      public Object transform(final Handlebars handlebars, final Scope scope,
          final Object candidate) {
        int size = Array.getLength(candidate);
        List<Object> list = new ArrayList<Object>(size);
        for (int i = 0; i < size; i++) {
          list.add(Array.get(candidate, i));
        }
        return list;
      }
    };

    protected boolean apply(final Object candidate) {
      return false;
    }

    public Object transform(final Handlebars handlebars, final Scope scope,
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
        body.merge(scope, writer);
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

    ITERABLE {
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
        boolean empty = true;
        for (Object element : elements) {
          body.merge(Scopes.scope(scope, element), writer);
          empty = false;
        }
        if (empty) {
          body.merge(scope, writer);
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
        return candidate instanceof SectionLambda;
      }

      @Override
      public boolean traverse(final Object candidate) {
        return true;
      }

      @Override
      public void merge(final Writer writer, final Template body,
          final Scope scope, final String name, final Object candidate)
          throws IOException {
        BaseTemplate template = ((SectionLambda) candidate).apply(scope, body);
        template.merge(scope, writer);
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

  private String type;

  private Handlebars handlebars;

  private String delimStart;

  private String delimEnd;

  public Section(final Handlebars handlebars, final String name,
      final boolean inverted) {
    this.handlebars = handlebars;
    this.name = name;
    this.inverted = inverted;
    this.type = inverted ? "^" : "#";
  }

  @Override
  public void merge(final Scope scope,
      final Writer writer) throws IOException {
    Object candidate = scope.get(name);
    candidate =
        Transformer.get(candidate).transform(handlebars, scope, candidate);
    if (candidate instanceof DelimAware) {
      ((DelimAware) candidate).setDelimiters(delimStart, delimEnd);
    }
    Type type = Type.get(candidate);
    boolean traverse = type.traverse(candidate);
    if (inverted) {
      traverse = !traverse;
    }
    if (traverse) {
      type.merge(writer, body, scope, name, candidate);
    }
  }

  public String name() {
    return name;
  }

  public boolean inverted() {
    return inverted;
  }

  @Override
  public boolean remove(final Template child) {
    return ((BaseTemplate) body).remove(child);
  }

  public Section body(final Template body) {
    this.body = body;
    return this;
  }

  public Section delimEnd(final String delimEnd) {
    this.delimEnd = delimEnd;
    return this;
  }

  public Section delimStart(final String delimStart) {
    this.delimStart = delimStart;
    return this;
  }

  public Template body() {
    return body;
  }

  @Override
  public String text() {
    String content = body == null ? "" : body.toString();
    return "{{" + type + name + "}}" + content + "{{/" + name + "}}";
  }

  public String delimStart() {
    return delimStart;
  }

  public String delimEnd() {
    return delimEnd;
  }
}
