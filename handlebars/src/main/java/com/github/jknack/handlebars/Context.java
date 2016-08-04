/**
 * Copyright (c) 2012-2015 Edgar Espina
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.jknack.handlebars.internal.path.ThisPath;
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
   * Special scope for silly block param rules, implemented by handlebars.js. This context will
   * check for pathed variable and resolve them against parent context.
   *
   * @author edgar
   * @since 3.0.0
   */
  private static class BlockParam extends Context {

    /**
     * A new {@link BlockParam}.
     *
     * @param parent Parent context.
     * @param hash A hash model.
     */
    protected BlockParam(final Context parent, final Map<String, Object> hash) {
      super(hash);
      this.extendedContext = new Context(new HashMap<String, Object>());
      this.extendedContext.resolver = parent.resolver;
      this.parent = parent;
      this.data = parent.data;
      this.resolver = parent.resolver;
    }

    @Override
    public Object get(final List<PathExpression> path) {
      String key = path.get(0).toString();
      // we must resolve this to parent context on block contexts (so tricky)
      if (path.size() == 1 && key.equals("this")) {
        return parent.model;
      }
      // path variable should resolve from parent :S
      if (key.startsWith(".")) {
        return parent.get(path.subList(1, path.size()));
      }
      return super.get(path);
    }

    @Override
    protected Context newChildContext(final Object model) {
      return new ParentFirst(model);
    }
  }

  /**
   * Context that resolve variables against parent, or fallback to default/normal lookup.
   *
   * @author edgar
   * @since 3.0.0
   */
  private static class ParentFirst extends Context {

    /**
     * Parent first lookup.
     *
     * @param model A model.
     */
    protected ParentFirst(final Object model) {
      super(model);
    }

    @Override
    public Object get(final List<PathExpression> path) {
      Object value = parent.get(path);
      if (value == null) {
        return super.get(path);
      }
      return value;
    }

    @Override
    protected Context newChildContext(final Object model) {
      return new ParentFirst(model);
    }
  }

  /**
   * Partial context.
   *
   * @author edgar
   * @since 4.0.5
   */
  private static class PartialCtx extends Context {

    /**
     * Creates a new partial context.
     *
     * @param parent Parent.
     * @param model Model.
     * @param hash Hash.
     */
    protected PartialCtx(final Context parent, final Object model, final Map<String, Object> hash) {
      super(model);
      this.extendedContext = new Context(hash);
      this.extendedContext.resolver = parent.resolver;
      this.extendedContext.extendedContext = new Context(Collections.emptyMap());
      this.parent = parent;
      this.data = parent.data;
      this.resolver = parent.resolver;
    }

    @Override
    public Object get(final List<PathExpression> path) {
      String key = path.get(0).toString();
      // hash first, except for this
      if (key.equals("this")) {
        return super.get(path);
      }
      Object value = extendedContext.get(path);
      if (value == null) {
        return super.get(path);
      }
      return value;
    }
  }

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
      int i = 0;
      while (i < resolvers.length) {
        Object value = resolvers[i].resolve(context, name);
        if (value != UNRESOLVED) {
          return value == null ? NULL : value;
        }
        i += 1;
      }
      return null;
    }

    @Override
    public Object resolve(final Object context) {
      int i = 0;
      while (i < resolvers.length) {
        Object value = resolvers[i].resolve(context);
        if (value != UNRESOLVED) {
          return value == null ? NULL : value;
        }
        i += 1;
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
      context = parent.newChild(model);
    }

    /**
     * Creates a new context builder.
     *
     * @param model The model data.
     */
    private Builder(final Object model) {
      context = Context.root(model);
      context.setResolver(new CompositeValueResolver(ValueResolver.VALUE_RESOLVERS));
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
    public Builder combine(final Map<String, ?> model) {
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
     * Add one or more value resolver to the defaults defined by
     * {@link ValueResolver#VALUE_RESOLVERS}.
     *
     * @param resolvers The value resolvers. Required.
     * @return This builder.
     */
    public Builder push(final ValueResolver... resolvers) {
      notEmpty(resolvers, "At least one value-resolver must be present.");
      List<ValueResolver> merged = new ArrayList<>();
      merged.addAll(Arrays.asList(ValueResolver.VALUE_RESOLVERS));
      merged.addAll(Arrays.asList(resolvers));
      context.setResolver(
          new CompositeValueResolver(merged.toArray(new ValueResolver[merged.size()])));
      return this;
    }

    /**
     * Build a context stack.
     *
     * @return A new context stack.
     */
    public Context build() {
      return context;
    }
  }

  /**
   * Path expression chain.
   *
   * @author edgar
   * @since 4.0.1
   */
  private static class PathExpressionChain implements PathExpression.Chain {

    /** Expression path. */
    private List<PathExpression> path;

    /** Cursor to move/execute the next expression. */
    private int i = 0;

    /**
     * Creates a new {@link PathExpressionChain}.
     *
     * @param path Expression path.
     */
    public PathExpressionChain(final List<PathExpression> path) {
      this.path = path;
    }

    @Override
    public Object next(final ValueResolver resolver, final Context context, final Object data) {
      if (data != null && i < path.size()) {
        PathExpression next = path.get(i++);
        return next.eval(resolver, context, data, this);
      }
      return data;
    }

    @Override
    public List<PathExpression> path() {
      return path.subList(i, path.size());
    }

    /**
     * Reset any previous state and restart the evaluation.
     *
     * @param resolver Value resolver.
     * @param context Context object.
     * @param data Data object.
     * @return A resolved value or <code>null</code>.
     */
    public Object eval(final ValueResolver resolver, final Context context, final Object data) {
      i = 0;
      Object value = next(resolver, context, data);
      if (value == null) {
        return i > 1 ? NULL : null;
      }
      return value;
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
   * Inline partials.
   */
  public static final String INLINE_PARTIALS = "__inline_partials_";

  /**
   * The qualified name for partials. Internal use.
   */
  public static final String INVOCATION_STACK = Context.class.getName() + "#invocationStack";

  /**
   * Number of parameters of a helper. Internal use.
   */
  public static final String PARAM_SIZE = Context.class.getName() + "#paramSize";

  /**
   * The parent context. Optional.
   */
  protected Context parent;

  /**
   * The target value. Resolved as '.' or 'this' inside templates. Required.
   */
  Object model;

  /**
   * A thread safe storage.
   */
  protected Map<String, Object> data;

  /**
   * Additional, data can be stored here.
   */
  protected Context extendedContext;

  /**
   * The value resolver.
   */
  protected ValueResolver resolver;

  /**
   * Creates a new context.
   *
   * @param model The target value. Resolved as '.' or 'this' inside templates. Required.
   */
  protected Context(final Object model) {
    this.model = model;
    this.extendedContext = null;
    this.parent = null;
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
    root.data = new HashMap<String, Object>();
    root.data.put(PARTIALS, new HashMap<String, Template>());
    LinkedList<Map<String, Template>> partials = new LinkedList<>();
    partials.push(new HashMap<String, Template>());
    root.data.put(INLINE_PARTIALS, partials);
    root.data.put(INVOCATION_STACK, new LinkedList<TemplateSource>());
    root.data.put("root", model);
    return root;
  }

  /**
   * Insert a new attribute in the context-stack.
   *
   * @param name The attribute's name. Required.
   * @param model The model data.
   * @return This context.
   */
  @SuppressWarnings({"unchecked" })
  public Context combine(final String name, final Object model) {
    Map<String, Object> map = (Map<String, Object>) extendedContext.model;
    map.put(name, model);
    return this;
  }

  /**
   * Insert all the attributes in the context-stack.
   *
   * @param model The model attributes.
   * @return This context.
   */
  @SuppressWarnings({"unchecked" })
  public Context combine(final Map<String, ?> model) {
    Map<String, Object> map = (Map<String, Object>) extendedContext.model;
    map.putAll(model);
    return this;
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
    data.putAll(attributes);
    return this;
  }

  /**
   * Resolved as '.' or 'this' inside templates.
   *
   * @return The model or data.
   */
  public final Object model() {
    return model;
  }

  /**
   * The parent context or null.
   *
   * @return The parent context or null.
   */
  public final Context parent() {
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
   * @return True, if this context is a block param context.
   */
  public boolean isBlockParams() {
    return this instanceof BlockParam;
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
   * @param path The object path.
   * @return The value associated to the given key or <code>null</code> if no value is found.
   */
  public Object get(final List<PathExpression> path) {
    PathExpression head = path.get(0);
    boolean local = head.local();
    Object value = null;
    PathExpressionChain expr = new PathExpressionChain(path);
    Context it = this;
    if (local) {
      value = expr.next(resolver, it, it.model);
      // extends local lookup to extended context if 'this' isn't present
      if (value == null && !(head instanceof ThisPath)) {
        value = expr.eval(resolver, it.extendedContext, it.extendedContext.model);
      }
    } else {
      while (value == null && it != null) {
        value = expr.eval(resolver, it, it.model);
        if (value == null) {
          // No luck, check the extended context.
          value = expr.eval(resolver, it.extendedContext, it.extendedContext.model);

          if (value == null) {
            // data context
            value = expr.eval(resolver, it, it.data);
          }
        }
        it = it.parent;
      }
    }
    return value == NULL ? null : value;
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
   * @return The value associated to the given key or <code>null</code> if no value is found.
   */
  public Object get(final String key) {
    return get(key, true);
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
   * @param parentScopeResolution False, if we want to restrict lookup to current scope.
   * @return The value associated to the given key or <code>null</code> if no value is found.
   */
  public Object get(final String key, final boolean parentScopeResolution) {
    return get(PathCompiler.compile(key, parentScopeResolution));
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
   * Creates a new block param context.
   *
   * @param parent The parent context. Required.
   * @param names A list of names to set in the block param context.
   * @param values A list of values to set in the block param context.
   * @return A new block param context.
   */
  public static Context newBlockParamContext(final Context parent, final List<String> names,
      final List<Object> values) {
    Map<String, Object> hash = new HashMap<String, Object>();
    for (int i = 0; i < Math.min(values.size(), names.size()); i++) {
      hash.put(names.get(i), values.get(i));
    }
    return new BlockParam(parent, hash);
  }

  /**
   * Creates a new partial context.
   *
   * @param ctx Current scope.
   * @param scope Scope switch.
   * @param hash Partial hash.
   * @return A new context.
   */
  public static Context newPartialContext(final Context ctx, final String scope,
      final Map<String, Object> hash) {
    return new PartialCtx(ctx, ctx.get(scope), hash);
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

  /**
   * Creates a new child context.
   *
   * @param model A model/data.
   * @return A new context.
   */
  private Context newChild(final Object model) {
    Context child = newChildContext(model);
    child.extendedContext = new Context(new HashMap<String, Object>());
    child.setResolver(this.resolver);
    child.parent = this;
    child.data = this.data;
    return child;
  }

  /**
   * Creates an empty/default context.
   *
   * @param model A model/data.
   * @return A new context.
   */
  protected Context newChildContext(final Object model) {
    return new Context(model);
  }

  /**
   * Creates a new context but keep the <code>data</code> attribute.
   *
   * @param context Context to extract the <code>data</code> attribute.
   * @param model A model/data.
   * @return A new context.
   */
  public static Context copy(final Context context, final Object model) {
    Context ctx = Context.newContext(model);
    ctx.data = context.data;
    ctx.resolver = context.resolver;
    return ctx;
  }

}
