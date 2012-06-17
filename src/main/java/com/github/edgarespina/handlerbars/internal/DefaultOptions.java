package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.edgarespina.handlerbars.Options;
import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Template;

public class DefaultOptions implements Options {

  private Scope context;

  private Template template;

  private Template inverse;

  private Object[] params;

  private Map<String, Object> hash;

  public DefaultOptions(final Template template, final Template inverse,
      final Scope context, final Object[] params,
      final Map<String, Object> hash) {
    this.template = template;
    this.inverse = inverse;
    this.context = context;
    this.params = params;
    this.hash = hash;
  }

  @Override
  public String fn() throws IOException {
    return fn(context);
  }

  @Override
  public String fn(final Object context) throws IOException {
    return apply(template, context);
  }

  @Override
  public String inverse() throws IOException {
    return inverse(context);
  }

  @Override
  public String inverse(final Object context) throws IOException {
    if (inverse == null) {
      return "";
    }

    return apply(inverse, context);
  }

  private String apply(final Template template, final Object context)
      throws IOException {
    if (context == null) {
      return "";
    }
    String result =
        template.apply(context == this.context ? this.context : Scopes.scope(
            this.context, context));
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T param(final int index) {
    return (T) params[index];
  }

  @Override
  public int paramSize() {
    return params.length;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T hash(final String name) {
    return (T) hash.get(name);
  }

  @Override
  public Set<Entry<String, Object>> hash() {
    return hash.entrySet();
  }

  @Override
  public boolean empty(final Object value) {
    return Empty.empty(value);
  }

  @Override
  public Template template() {
    return template;
  }

  public void destroy() {
    this.hash = null;
    this.params = null;
    this.context = null;
    this.template = null;
  }
}
