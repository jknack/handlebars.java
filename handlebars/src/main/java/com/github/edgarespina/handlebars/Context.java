/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.edgarespina.handlebars;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.github.edgarespina.handlebars.context.JavaBeanValueResolver;
import com.github.edgarespina.handlebars.context.MapValueResolver;

/**
 * Mustache/Handlabars are contextual template engines. This class represent the
 * 'context stack' of a template.
 * <ul>
 * <li>Objects and hashes should be pushed onto the context stack.
 * <li>All elements on the context stack should be accessible.
 * <li>Multiple sections per template should be permitted.
 * <li>Failed context lookups should be considered falsey.
 * <li>Dotted names should be valid for Section tags.
 * <li>Dotted names that cannot be resolved should be considered falsey.
 * <li>Dotted Names - Context Precedence: Dotted names should be resolved
 * against former resolutions.
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public final class Context {

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
      this.context = Context.child(parent, model);
    }

    /**
     * Creates a new context builder.
     *
     * @param model The model data.
     */
    private Builder(final Object model) {
      this.context = Context.root(model);
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
      if (resolvers.length == 0) {
        throw new IllegalArgumentException(
            "At least one value-resolver must be present.");
      }
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
              new CompositeValueResolver(MapValueResolver.INSTANCE,
                  JavaBeanValueResolver.INSTANCE));
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
   * The qualified name for partials. Internal use.
   */
  public static final String PARTIALS = Context.class.getName() + "#partials";

  /**
   * The parent context. Optional.
   */
  private Context parent;

  /**
   * The target value. Resolved as '.' or 'this' inside templates. Required.
   */
  private final Object model;

  /**
   * A thread safe storage.
   */
  private Map<String, Object> storage;

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
  private Context(final Object model) {
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
    root.storage = new HashMap<String, Object>();
    root.storage.put(PARTIALS, new HashMap<String, Template>());
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
    checkNotNull(parent, "A parent context is required.");
    Context child = new Context(model);
    child.extendedContext = new Context(new HashMap<String, Object>());
    child.parent = parent;
    child.storage = parent.storage;
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
    checkNotNull(name, "The variable's name is required.");
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
   * A contextual storage useful for saving values in a thread-safety way. The
   * storage is cleaned up once a template has been rendered.
   *
   * @return A contextual storage. Never null.
   */
  public Map<String, Object> storage() {
    return storage;
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
   * Lookup the given key inside the context stack.
   * <ul>
   * <li>Objects and hashes should be pushed onto the context stack.
   * <li>All elements on the context stack should be accessible.
   * <li>Multiple sections per template should be permitted.
   * <li>Failed context lookups should be considered falsey.
   * <li>Dotted names should be valid for Section tags.
   * <li>Dotted names that cannot be resolved should be considered falsey.
   * <li>Dotted Names - Context Precedence: Dotted names should be resolved
   * against former resolutions.
   * </ul>
   *
   * @param key The object key.
   * @return The value associated to the given key or <code>null</code> if no
   *         value is found.
   */
  public Object get(final Object key) {
    if (".".equals(key) || "this".equals(key)) {
      return model;
    }
    String[] path = toPath(key);
    Object value = get(path);
    if (value == null) {
      // No luck, check the extended context.
      value = get(extendedContext, key);
      if (value == null) {
        // No luck, check the parent context.
        value = get(parent, key);
      }
    }
    return value == NULL ? null : value;
  }

  /**
   * Look for the speficied key in an external context.
   *
   * @param external The external context.
   * @param key The associated key.
   * @return The associated value or null if not found.
   */
  private Object get(final Context external, final Object key) {
    if (external != null) {
      return external.get(key);
    }
    return null;
  }

  /**
   * Split the property name by '.' and create an array of it.
   *
   * @param key The property's name.
   * @return A path representation of the property (array based).
   */
  private String[] toPath(final Object key) {
    StringTokenizer tokenizer = new StringTokenizer(key.toString(), ".");
    int len = tokenizer.countTokens();
    if (len == 1) {
      return new String[] {key.toString() };
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
    for (int i = 0; i < path.length - 1; i++) {
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
   * Do the actual lookup of an unqualify property name.
   *
   * @param current The target object.
   * @param name The property's name.
   * @return The associated value.
   */
  private Object resolve(final Object current, final String name) {
    return current == null ? null : resolver.resolve(current, name);
  }

  /**
   * Set the value resolver and propagate it to the extendedContext.
   *
   * @param resolver The value resolver.
   */
  private void setResolver(final ValueResolver resolver) {
    this.resolver = resolver;
    this.extendedContext.resolver = resolver;
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
    checkNotNull(parent, "The parent context is required.");
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
