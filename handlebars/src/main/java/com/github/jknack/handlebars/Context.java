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
package com.github.jknack.handlebars;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Mustache/Handlebars are contextual template engines. This class represent the
 * 'context stack' of a template.
 * <ul>
 * <li>Objects and hashes should be pushed onto the context stack.
 * <li>All elements on the context stack should be accessible.
 * <li>Multiple sections per template should be permitted.
 * <li>Failed context lookups should be considered falsy.
 * <li>Dotted names should be valid for Section tags.
 * <li>Dotted names that cannot be resolved should be considered falsy.
 * <li>Dotted Names - Context Precedence: Dotted names should be resolved against former
 * resolutions.
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class Context {

  /**
   * Handlebars and Mustache path separator.
   */
  private static final String PATH_SEPARATOR = "./";

  /**
   * Handlebars 'parent' attribute reference.
   */
  private static final String PARENT_ATTR = "../";

  /**
   * Handlebars 'parent' attribute reference.
   */
  private static final String PARENT = "..";

  /**
   * Handlebars 'this' reference.
   */
  private static final String THIS = "this";

  /**
   * The mustache 'this' reference.
   */
  private static final String MUSTACHE_THIS = ".";

  /**
   * A composite value resolver. It delegate the value resolution.
   *
   * @author edgar.espina
   * @since 0.1.1
   */
  private static class CompositeValueResolver implements ValueResolver {

    /**
     * The internal value resolvers.
     */
    private ValueResolver[] resolvers;

    /**
     * Creates a new {@link CompositeValueResolver}.
     *
     * @param resolvers The value resolvers.
     */
    public CompositeValueResolver(final ValueResolver... resolvers) {
      this.resolvers = resolvers;
    }

    @Override
    public Object resolve(final Object context, final String name) {
      for (ValueResolver resolver : resolvers) {
        Object value = resolver.resolve(context, name);
        if (value != UNRESOLVED) {
          return value == null ? NULL : value;
        }
      }
      return null;
    }

    @Override
    public Set<Entry<String, Object>> propertySet(final Object context) {
      Set<Entry<String, Object>> propertySet = new LinkedHashSet<Map.Entry<String, Object>>();
      for (ValueResolver resolver : resolvers) {
        propertySet.addAll(resolver.propertySet(context));
      }
      return propertySet;
    }
  }

  /**
   * A context builder.
   *
   * @author edgar.espina
   * @since 0.1.1
   */
  public static final class Builder {

    /**
     * The context product.
     */
    private Context context;

    /**
     * Creates a new context builder.
     *
     * @param parent The parent context. Required.
     * @param model The model data.
     */
    private Builder(final Context parent, final Object model) {
      context = Context.child(parent, model);
    }

    /**
     * Creates a new context builder.
     *
     * @param model The model data.
     */
    private Builder(final Object model) {
      context = Context.root(model);
    }

    /**
     * Combine the given model using the specified name.
     *
     * @param name The variable's name. Required.
     * @param model The model data.
     * @return This builder.
     */
    public Builder combine(final String name, final Object model) {
      context.combine(name, model);
      return this;
    }

    /**
     * Combine all the map entries into the context stack.
     *
     * @param model The model data.
     * @return This builder.
     */
    public Builder combine(final Map<String, Object> model) {
      context.combine(model);
      return this;
    }

    /**
     * Set the value resolvers to use.
     *
     * @param resolvers The value resolvers. Required.
     * @return This builder.
     */
    public Builder resolver(final ValueResolver... resolvers) {
      notEmpty(resolvers, "At least one value-resolver must be present.");
      context.setResolver(new CompositeValueResolver(resolvers));
      return this;
    }

    /**
     * Build a context stack.
     *
     * @return A new context stack.
     */
    public Context build() {
      if (context.resolver == null) {
        if (context.parent != null) {
          // Set resolver from parent.
          context.resolver = context.parent.resolver;
        } else {
          // Set default value resolvers: Java Bean like and Map resolvers.
          context.setResolver(
              new CompositeValueResolver(ValueResolver.VALUE_RESOLVERS));
        }
        // Expand resolver to the extended context.
        if (context.extendedContext != null) {
          context.extendedContext.resolver = context.resolver;
        }
      }
      return context;
    }
  }

  /**
   * Mark for fail context lookup.
   */
  private static final Object NULL = new Object();

  /**
   * Property access expression.
   */
  private static final Pattern IDX = Pattern.compile("\\[((.)+)\\]");

  /**
   * Index access expression.
   */
  private static final Pattern INT = Pattern.compile("\\d+");

  /**
   * The qualified name for partials. Internal use.
   */
  public static final String PARTIALS = Context.class.getName() + "#partials";

  /**
   * The qualified name for partials. Internal use.
   */
  public static final String INVOCATION_STACK = Context.class.getName() + "#invocationStack";

  /**
   * The parent context. Optional.
   */
  private Context parent;

  /**
   * The target value. Resolved as '.' or 'this' inside templates. Required.
   */
  private Object model;

  /**
   * A thread safe storage.
   */
  private Map<String, Object> data;

  /**
   * Additional, data can be stored here.
   */
  private Context extendedContext;

  /**
   * The value resolver.
   */
  private ValueResolver resolver;

  /**
   * Creates a new context.
   *
   * @param model The target value. Resolved as '.' or 'this' inside
   *        templates. Required.
   */
  protected Context(final Object model) {
    if (model instanceof Context) {
      throw new IllegalArgumentException("Invalid model type:"
          + model.getClass().getName());
    }
    this.model = model;
  }

  /**
   * Creates a root context.
   *
   * @param model The target value. Resolved as '.' or 'this' inside
   *        templates. Required.
   * @return A root context.
   */
  private static Context root(final Object model) {
    Context root = new Context(model);
    root.extendedContext = new Context(new HashMap<String, Object>());
    root.parent = null;
    root.data = new HashMap<String, Object>();
    root.data.put(PARTIALS, new HashMap<String, Template>());
    root.data.put(INVOCATION_STACK, new LinkedList<TemplateSource>());
    return root;
  }

  /**
   * Creates a child context.
   *
   * @param parent The parent context. Required.
   * @param model The target value. Resolved as '.' or 'this' inside
   *        templates. Required.
   * @return A child context.
   */
  private static Context child(final Context parent, final Object model) {
    notNull(parent, "A parent context is required.");
    Context child = new Context(model);
    child.extendedContext = new Context(new HashMap<String, Object>());
    child.parent = parent;
    child.data = parent.data;
    return child;
  }

  /**
   * Insert a new attribute in the context-stack.
   *
   * @param name The attribute's name. Required.
   * @param model The model data.
   */
  @SuppressWarnings({"unchecked" })
  private void combine(final String name, final Object model) {
    notEmpty(name, "The variable's name is required.");
    Map<String, Object> map = (Map<String, Object>) extendedContext.model;
    map.put(name, model);
  }

  /**
   * Inser all the attributes in the context-stack.
   *
   * @param model The model attributes.
   */
  @SuppressWarnings({"unchecked" })
  private void combine(final Map<String, Object> model) {
    Map<String, Object> map = (Map<String, Object>) extendedContext.model;
    map.putAll(model);
  }

  /**
   * Read the attribute from the data storage.
   *
   * @param name The attribute's name.
   * @param <T> Data type.
   * @return The attribute value or null.
   */
  @SuppressWarnings("unchecked")
  public <T> T data(final String name) {
    return (T) data.get(name);
  }

  /**
   * Set an attribute in the data storage.
   *
   * @param name The attribute's name. Required.
   * @param value The attribute's value. Required.
   * @return This context.
   */
  public Context data(final String name, final Object value) {
    notEmpty(name, "The attribute's name is required.");
    data.put(name, value);
    return this;
  }

  /**
   * Store the map in the data storage.
   *
   * @param attributes The attributes to add. Required.
   * @return This context.
   */
  public Context data(final Map<String, ?> attributes) {
    notNull(attributes, "The attributes are required.");
    data.putAll(attributes);
    return this;
  }

  /**
   * Resolved as '.' or 'this' inside templates.
   *
   * @return The model or data.
   */
  public Object model() {
    return model;
  }

  /**
   * The parent context or null.
   *
   * @return The parent context or null.
   */
  public Context parent() {
    return parent;
  }

  /**
   * List all the properties and values for the given object.
   *
   * @param context The context object.
   * @return All the properties and values for the given object.
   */
  public Set<Entry<String, Object>> propertySet(final Object context) {
    if (context == null) {
      return Collections.emptySet();
    }
    if (context instanceof Context) {
      return resolver.propertySet(((Context) context).model);
    }
    return resolver.propertySet(context);
  }

  /**
   * List all the properties and values of {@link #model()}.
   *
   * @return All the properties and values of {@link #model()}.
   */
  public Set<Entry<String, Object>> propertySet() {
    return propertySet(model);
  }

  /**
   * Lookup the given key inside the context stack.
   * <ul>
   * <li>Objects and hashes should be pushed onto the context stack.
   * <li>All elements on the context stack should be accessible.
   * <li>Multiple sections per template should be permitted.
   * <li>Failed context lookups should be considered falsey.
   * <li>Dotted names should be valid for Section tags.
   * <li>Dotted names that cannot be resolved should be considered falsey.
   * <li>Dotted Names - Context Precedence: Dotted names should be resolved against former
   * resolutions.
   * </ul>
   *
   * @param key The object key.
   * @return The value associated to the given key or <code>null</code> if no
   *         value is found.
   */
  public Object get(final String key) {
    // '.' or 'this'
    if (MUSTACHE_THIS.equals(key) || THIS.equals(key)) {
      return model;
    }
    // '..'
    if (key.equals(PARENT)) {
      return parent == null ? null : parent.model;
    }
    // '../'
    if (key.startsWith(PARENT_ATTR)) {
      return parent == null ? null : parent.get(key.substring(PARENT_ATTR.length()));
    }
    String[] path = toPath(key);
    Object value = get(path);
    if (value == null) {
      // No luck, check the extended context.
      value = get(extendedContext, key);
      // No luck, check the data context.
      if (value == null && data != null) {
        String dataKey = key.charAt(0) == '@' ? key.substring(1) : key;
        // simple data keys will be resolved immediately, complex keys need to go down and using a
        // new context.
        value = data.get(dataKey);
        if (value == null && path.length > 1) {
          // for complex keys, a new data context need to be created per invocation,
          // bc data might changes per execution.
          Context dataContext = Context.newBuilder(data).resolver(MapValueResolver.INSTANCE)
              .build();
          // don't extend the lookup further.
          dataContext.data = null;
          value = dataContext.get(dataKey);
          // destroy it!
          dataContext.destroy();
        }
      }
      // No luck, but before checking at the parent scope we need to check for
      // the 'this' qualifier. If present, no look up will be done.
      if (value == null && !path[0].equals(THIS)) {
        value = get(parent, key);
      }
    }
    return value == NULL ? null : value;
  }

  /**
   * Look for the specified key in an external context.
   *
   * @param external The external context.
   * @param key The associated key.
   * @return The associated value or null if not found.
   */
  private Object get(final Context external, final String key) {
    return external == null ? null : external.get(key);
  }

  /**
   * Split the property name by '.' and create an array of it.
   *
   * @param key The property's name.
   * @return A path representation of the property (array based).
   */
  private String[] toPath(final String key) {
    StringTokenizer tokenizer = new StringTokenizer(key, PATH_SEPARATOR);
    int len = tokenizer.countTokens();
    if (len == 1) {
      return new String[]{key.toString() };
    }
    String[] path = new String[len];
    int i = 0;
    while (tokenizer.hasMoreTokens()) {
      path[i++] = tokenizer.nextToken();
    }
    return path;
  }

  /**
   * Iterate over the qualified path and return a value. The value can be
   * null, {@link #NULL} or not null. If the value is <code>null</code>, the
   * value isn't present and the lookup algorithm will searchin for the value in
   * the parent context.
   * If the value is {@value #NULL} the search must stop bc the context for
   * the given path exists but there isn't a value there.
   *
   * @param path The qualified path.
   * @return The value inside the stack for the given path.
   */
  private Object get(final String[] path) {
    Object current = model;
    // Resolve 'this' to the current model.
    int start = path[0].equals(THIS) ? 1 : 0;
    for (int i = start; i < path.length - 1; i++) {
      current = resolve(current, path[i]);
      if (current == null) {
        return null;
      }
    }
    String name = path[path.length - 1];
    Object value = resolve(current, name);
    if (value == null && current != model) {
      // We're looking in the right scope, but the value isn't there
      // returns a custom mark to stop looking
      value = NULL;
    }
    return value;
  }

  /**
   * Do the actual lookup of an unqualified property name.
   *
   * @param current The target object.
   * @param expression The access expression.
   * @return The associated value.
   */
  @SuppressWarnings("rawtypes")
  private Object resolve(final Object current, final String expression) {
    // Null => null
    if (current == null) {
      return null;
    }

    // array or list access?
    Matcher matcher = IDX.matcher(expression);
    if (matcher.matches()) {
      String idx = matcher.group(1);
      if (INT.matcher(idx).matches()) {
        // It is a number, check if the current value is a index base object.
        int pos = Integer.parseInt(idx);
        if (current instanceof List) {
          return ((List) current).get(pos);
        } else if (current.getClass().isArray()) {
          return Array.get(current, pos);
        }
      }
      // It is not a index base object, defaults to string property lookup
      // (usually not a valid Java identifier)
      return resolver.resolve(current, idx);
    }
    return resolver.resolve(current, expression);
  }

  /**
   * Set the value resolver and propagate it to the extendedContext.
   *
   * @param resolver The value resolver.
   */
  private void setResolver(final ValueResolver resolver) {
    this.resolver = resolver;
    extendedContext.resolver = resolver;
  }

  /**
   * Destroy this context by cleaning up instance attributes.
   */
  public void destroy() {
    model = null;
    if (parent == null) {
      // Root context is the owner of the storage.
      if (data != null) {
        data.clear();
      }
    }
    if (extendedContext != null) {
      extendedContext.destroy();
    }
    parent = null;
    resolver = null;
    data = null;
  }

  @Override
  public String toString() {
    return String.valueOf(model);
  }

  /**
   * Start a new context builder.
   *
   * @param parent The parent context. Required.
   * @param model The model data.
   * @return A new context builder.
   */
  public static Builder newBuilder(final Context parent, final Object model) {
    notNull(parent, "The parent context is required.");
    return new Builder(parent, model);
  }

  /**
   * Start a new context builder.
   *
   * @param model The model data.
   * @return A new context builder.
   */
  public static Builder newBuilder(final Object model) {
    return new Builder(model);
  }

  /**
   * Creates a new child context.
   *
   * @param parent The parent context. Required.
   * @param model The model data.
   * @return A new child context.
   */
  public static Context newContext(final Context parent, final Object model) {
    return newBuilder(parent, model).build();
  }

  /**
   * Creates a new root context.
   *
   * @param model The model data.
   * @return A new root context.
   */
  public static Context newContext(final Object model) {
    return newBuilder(model).build();
  }

}
