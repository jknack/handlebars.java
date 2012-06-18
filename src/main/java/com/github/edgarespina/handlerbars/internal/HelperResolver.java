package com.github.edgarespina.handlerbars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.Helper;
import com.github.edgarespina.handlerbars.Template;

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
    this.handlebars =
        checkNotNull(handlebars, "A handlebars instance is required.");
  }

  /**
   * Build a hash object by looking for values in the current context.
   *
   * @param context The current context.
   * @return A hash object with values in the current context.
   */
  protected Map<String, Object> hash(final Context context) {
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    for (Entry<String, Object> entry : this.hash.entrySet()) {
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
      value = ParamType.parse(scope, value);
      values[i - 1] = value;
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
      return context.target();
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
      throw new HandlebarsException("Could not find helper: '" + name + "'");
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
}
