/**
 * Copyright (c) 2012-2013 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;

/**
 * Base class for {@link Template} who need to resolver {@link Helper}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
abstract class HelperResolver extends BaseTemplate {

  /**
   * The handlebars object. Required.
   */
  protected final Handlebars handlebars;

  /**
   * The parameter list.
   */
  private List<Object> params = Collections.emptyList();

  /**
   * The hash object.
   */
  private Map<String, Object> hash = Collections.emptyMap();

  /**
   * Empty parameters.
   */
  private static final Object[] PARAMS = {};

  /**
   * Creates a new {@link HelperResolver}.
   *
   * @param handlebars The handlebars object. Required.
   */
  public HelperResolver(final Handlebars handlebars) {
    this.handlebars = notNull(handlebars, "The handlebars can't be null.");
  }

  /**
   * Build a hash object by looking for values in the current context.
   *
   * @param context The current context.
   * @return A hash object with values in the current context.
   */
  protected Map<String, Object> hash(final Context context) {
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    for (Entry<String, Object> entry : hash.entrySet()) {
      Object value = entry.getValue();
      value = ParamType.parse(context, value);
      result.put(entry.getKey(), value);
    }
    return result;
  }

  /**
   * Build a parameter list by looking for values in the current context.
   *
   * @param scope The current context.
   * @return A parameter list with values in the current context.
   */
  protected Object[] params(final Context scope) {
    if (params.size() <= 1) {
      return PARAMS;
    }
    Object[] values = new Object[params.size() - 1];
    for (int i = 1; i < params.size(); i++) {
      Object value = params.get(i);
      Object resolved = ParamType.parse(scope, value);
      values[i - 1] = resolved == null && handlebars.allowStringParams()
          ? value : resolved;
    }
    return values;
  }

  /**
   * Determine the current context. If the param list is empty, the current
   * context value is returned.
   *
   * @param context The current context.
   * @return The current context.
   */
  protected Object determineContext(final Context context) {
    if (params.size() == 0) {
      return context.model();
    }
    Object value = params.get(0);
    value = ParamType.parse(context, value);
    return value;
  }

  /**
   * Transform the given value (if applies).
   *
   * @param value The candidate value.
   * @return The value transformed (if applies).
   */
  protected Object transform(final Object value) {
    return Transformer.transform(value);
  }

  /**
   * Find the helper by it's name.
   *
   * @param name The helper's name.
   * @return The matching helper.
   */
  protected Helper<Object> helper(final String name) {
    Helper<Object> helper = handlebars.helper(name);
    if (helper == null && (params.size() > 0 || hash.size() > 0)) {
      Helper<Object> helperMissing =
          handlebars.helper(Handlebars.HELPER_MISSING);
      if (helperMissing == null) {
        throw new IllegalArgumentException("could not find helper: '" + name
            + "'");
      }
      helper = helperMissing;
    }
    return helper;
  }

  /**
   * Set the hash.
   *
   * @param hash The new hash.
   * @return This resolver.
   */
  public HelperResolver hash(final Map<String, Object> hash) {
    if (hash == null || hash.size() == 0) {
      this.hash = Collections.emptyMap();
    } else {
      this.hash = new LinkedHashMap<String, Object>(hash);
    }
    return this;
  }

  /**
   * Set the parameters.
   *
   * @param params The new params.
   * @return This resolver.
   */
  public HelperResolver params(final List<Object> params) {
    if (params == null || params.size() == 0) {
      this.params = Collections.emptyList();
    } else {
      this.params = new ArrayList<Object>(params);
    }
    return this;
  }

  /**
   * Make a string of {@link #params}.
   *
   * @return Make a string of {@link #params}.
   */
  protected String paramsToString() {
    if (params.size() > 0) {
      StringBuilder buffer = new StringBuilder();
      String sep = " ";
      for (Object param : params) {
        buffer.append(param).append(sep);
      }
      buffer.setLength(buffer.length() - sep.length());
      return buffer.toString();
    }
    return "";
  }

  /**
   * Make a string of {@link #hash}.
   *
   * @return Make a string of {@link #hash}.
   */
  protected String hashToString() {
    if (hash.size() > 0) {
      StringBuilder buffer = new StringBuilder();
      String sep = " ";
      for (Entry<String, Object> hash : this.hash.entrySet()) {
        buffer.append(hash.getKey()).append("=").append(hash.getValue())
            .append(sep);
      }
      buffer.setLength(buffer.length() - sep.length());
      return buffer.toString();
    }
    return "";
  }

}
