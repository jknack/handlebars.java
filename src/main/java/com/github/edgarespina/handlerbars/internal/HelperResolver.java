package com.github.edgarespina.handlerbars.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.Helper;
import com.github.edgarespina.handlerbars.Scope;

public abstract class HelperResolver extends BaseTemplate {

  protected final Handlebars handlebars;

  private List<Object> params = Collections.emptyList();

  private Map<String, Object> hash = Collections.emptyMap();

  private static final Object[] PARAMS = {};

  public HelperResolver(final Handlebars handlebars) {
    this.handlebars = handlebars;
  }

  protected Map<String, Object> hash(final Scope scope) {
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    for (Entry<String, Object> entry : this.hash.entrySet()) {
      Object value = entry.getValue();
      value = ParamType.get(value).parse(scope, value);
      result.put(entry.getKey(), value);
    }
    return result;
  }

  protected Object[] params(final Scope scope) {
    if (params.size() == 0) {
      return PARAMS;
    }
    Object[] values = new Object[params.size() - 1];
    for (int i = 1; i < params.size(); i++) {
      Object value = params.get(i);
      value = ParamType.get(value).parse(scope, value);
      values[i - 1] = value;
    }
    return values;
  }

  protected Object param(final Scope scope, final int index) {
    if (params.size() == 0) {
      return scope;
    }
    Object value = params.get(index);
    value = ParamType.get(value).parse(scope, value);
    return value;
  }

  protected Object transform(final Object value) {
    Object newValue = Transformer.get(value).transform(value);
    return newValue;
  }

  protected Helper<Object> helper(final String name) {
    Helper<Object> helper = handlebars.helper(name);
    if (helper == null && (params.size() > 0 || hash.size() > 0)) {
      throw new HandlebarsException("Could not find helper: '" + name + "'");
    }
    return helper;
  }

  public HelperResolver hash(final Map<String, Object> hash) {
    if (hash == null || hash.size() == 0) {
      this.hash = Collections.emptyMap();
    } else {
      this.hash = new LinkedHashMap<String, Object>(hash);
    }
    return this;
  }

  public HelperResolver params(final List<Object> params) {
    if (params == null || params.size() == 0) {
      this.params = Collections.emptyList();
    } else {
      this.params = new ArrayList<Object>(params);
    }
    return this;
  }
}
