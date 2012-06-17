package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Helper;
import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Template;

class Variable extends HelperResolver {

  private final String name;

  private final boolean escape;

  private final Object constant;

  public Variable(final Handlebars handlebars, final String name,
      final boolean escape, final List<Object> params,
      final Map<String, Object> hash) {
    this(handlebars, name, null, escape, params, hash);
  }

  public Variable(final Handlebars handlebars, final String name,
      final Object value, final boolean escape, final List<Object> params,
      final Map<String, Object> hash) {
    super(handlebars);
    this.name = name.trim();
    this.constant = value;
    this.escape = escape;
    params(params);
    hash(hash);
  }

  @SuppressWarnings("unchecked")
  public Variable(final Handlebars handlebars, final String name,
      final Object value, final boolean escape) {
    this(handlebars, name, value, escape, Collections.EMPTY_LIST,
        Collections.EMPTY_MAP);
  }

  public String name() {
    return name;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void apply(final Scope scope, final Writer writer) throws IOException {
    Helper<Object> helper = helper(name);
    if (helper != null) {
      Object context = param(scope, 0);
      DefaultOptions options =
          new DefaultOptions(this, null, scope, params(scope), hash(scope));
      CharSequence result = helper.apply(context, options);
      if (escape(result)) {
        writer.append(Handlebars.Utils.escapeExpression(result));
      } else {
        writer.append(result);
      }
    } else {
      Object value = this.constant == null ? scope.get(name) : this.constant;
      if (value != null) {
        if (value instanceof Lambda) {
          value =
              Lambdas.merge(handlebars, (Lambda<Object>) value, scope, this);
        }
        String stringValue = value.toString();
        // TODO: Add formatter hook
        if (escape(value)) {
            writer.append(Handlebars.Utils.escapeExpression(stringValue));
        } else {
          // DON'T escape none String values.
          writer.append(stringValue);
        }
      }
    }
  }

  private boolean escape(final Object value) {
    boolean isString =
        value instanceof CharSequence || value instanceof Character;
    if (isString) {
      return this.escape && !(value instanceof Handlebars.SafeString);
    } else {
      return false;
    }
  }

  @Override
  public boolean remove(final Template child) {
    return false;
  }

  @Override
  public String text() {
    return "{{" + name + "}}";
  }
}
