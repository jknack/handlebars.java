/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.internal.path.DataPath;
import com.github.jknack.handlebars.internal.path.ParentPath;
import com.github.jknack.handlebars.internal.path.PropertyPath;
import com.github.jknack.handlebars.internal.path.ThisPath;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Mustache/Handlebars are contextual template engines. This class represent the 'context stack' of
 * a template.
 *
 * <ul>
 *   <li>Objects and hashes should be pushed onto the context stack.
 *   <li>All elements on the context stack should be accessible.
 *   <li>Multiple sections per template should be permitted.
 *   <li>Failed context lookups should be considered falsy.
 *   <li>Dotted names should be valid for Section tags.
 *   <li>Dotted names that cannot be resolved should be considered falsy.
 *   <li>Dotted Names - Context Precedence: Dotted names should be resolved against former
 *       resolutions.
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
      this.data = new HashMap<>();
      this.dataParent = parent;
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
      if (key.startsWith(".") && path.size() > 1) {
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
      // Only block params (lexically scoped) take precedence over the current model. Everything
      // else resolves child first, like any other context.
      PathExpression head = path.get(0);
      if (head instanceof PropertyPath || (head instanceof ThisPath && path.size() > 1)) {
        PathExpressionChain expr = new PathExpressionChain(path);
        for (Context it = parent; it != null; it = it.parent) {
          if (it instanceof BlockParam) {
            Object value = expr.eval(resolver, it, it.model);
            if (value != null && value != NULL) {
              return value;
            }
          }
        }
      }
      return super.get(path);
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

    private final Map<String, Object> hash;

    /**
     * Creates a new partial context.
     *
     * @param parent Parent.
     * @param model Model.
     * @param hash Hash.
     */
    protected PartialCtx(final Context parent, final Object model, final Map<String, Object> hash) {
      super(model);
      this.hash = hash;
      this.extendedContext = new Context(hash);
      this.extendedContext.resolver = parent.resolver;
      this.extendedContext.extendedContext = new Context(Collections.emptyMap());
      this.parent = parent;
      this.data = new HashMap<>();
      this.dataParent = parent;
      this.resolver = parent.resolver;
    }

    @Override
    public Object get(final List<PathExpression> path) {
      String key = path.get(0).toString();
      // hash first, except for this
      if (path.get(0) instanceof ThisPath && path.size() == 1 || key.equals("this")) {
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

    /** The internal value resolvers. */
    private List<ValueResolver> resolvers;

    /**
     * Creates a new {@link CompositeValueResolver}.
     *
     * @param resolvers The value resolvers.
     */
    CompositeValueResolver(final List<ValueResolver> resolvers) {
      this.resolvers = resolvers;
    }

    @Override
    public Object resolve(final Object context, final String name) {
      int i = 0;
      while (i < resolvers.size()) {
        Object value = resolvers.get(i).resolve(context, name);
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
      while (i < resolvers.size()) {
        Object value = resolvers.get(i).resolve(context);
        if (value != UNRESOLVED) {
          return value == null ? NULL : value;
        }
        i += 1;
      }
      return null;
    }

    @Override
    public Set<Entry<String, Object>> propertySet(final Object context) {
      Set<Entry<String, Object>> propertySet = new LinkedHashSet<>();
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

    /** The context product. */
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
      context.setResolver(new CompositeValueResolver(ValueResolver.defaultValueResolvers()));
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
      boolean mapResolver = Stream.of(resolvers).anyMatch(MapValueResolver.class::isInstance);
      if (!mapResolver) {
        context.setResolver(
            new CompositeValueResolver(
                Stream.concat(Stream.of(resolvers), Stream.of(MapValueResolver.INSTANCE))
                    .collect(Collectors.toList())));
      } else {
        context.setResolver(new CompositeValueResolver(Arrays.asList(resolvers)));
      }
      return this;
    }

    /**
     * Add one or more value resolver to the defaults defined by {@link
     * ValueResolver#defaultValueResolvers()}.
     *
     * @param resolvers The value resolvers. Required.
     * @return This builder.
     */
    public Builder push(final ValueResolver... resolvers) {
      notEmpty(resolvers, "At least one value-resolver must be present.");
      List<ValueResolver> merged = new ArrayList<>();
      merged.addAll(ValueResolver.defaultValueResolvers());
      Stream.of(resolvers).forEach(merged::add);
      context.setResolver(new CompositeValueResolver(merged));
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
    PathExpressionChain(final List<PathExpression> path) {
      this.path = path;
    }

    @Override
    public Object next(final ValueResolver resolver, final Context context, final Object data) {
      if (i < path.size() && (data != null || path.get(i) instanceof ParentPath)) {
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

  /** Mark for fail context lookup. */
  private static final Object NULL = new Object();

  /** The qualified name for partials. Internal use. */
  public static final String PARTIALS = Context.class.getName() + "#partials";

  /** Inline partials. */
  public static final String INLINE_PARTIALS = "__inline_partials_";

  /** The qualified name for partials. Internal use. */
  public static final String INVOCATION_STACK = Context.class.getName() + "#invocationStack";

  /** Number of parameters of a helper. Internal use. */
  public static final String PARAM_SIZE = Context.class.getName() + "#paramSize";

  /** Last callee of a partial block. Internal use. */
  public static final String CALLEE = Context.class.getName() + "#callee";

  /** The parent context. Optional. */
  protected Context parent;

  /** The target value. Resolved as '.' or 'this' inside templates. Required. */
  Object model;

  /** A thread safe storage. */
  protected Map<String, Object> data;

  /** Parent of the data frame: lookups walk up this chain, writes only touch {@link #data}. */
  protected Context dataParent;

  /** Additional, data can be stored here. */
  protected Context extendedContext;

  /** The value resolver. */
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
   * @param model The target value. Resolved as '.' or 'this' inside templates. Required.
   * @return A root context.
   */
  private static Context root(final Object model) {
    Context root = new Context(model);
    root.extendedContext = new Context(new HashMap<String, Object>());
    root.data = new HashMap<>();
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
  @SuppressWarnings({"unchecked"})
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
  @SuppressWarnings({"unchecked"})
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
    for (Context frame = this; frame != null; frame = frame.dataParent) {
      if (frame.data != null && frame.data.containsKey(name)) {
        return (T) frame.data.get(name);
      }
    }
    return null;
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
   * Set an engine-internal attribute. Unlike {@link #data(String, Object)}, the value replaces the
   * one in the frame that already owns the attribute (or the current frame if none does), so
   * save/restore sequences don't leave stale copies in nested frames.
   *
   * @param name The attribute's name. Required.
   * @param value The attribute's value.
   */
  public void setData(final String name, final Object value) {
    for (Context frame = this; frame != null; frame = frame.dataParent) {
      if (frame.data != null && frame.data.containsKey(name)) {
        frame.data.put(name, value);
        return;
      }
    }
    data.put(name, value);
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
   *
   * <ul>
   *   <li>Objects and hashes should be pushed onto the context stack.
   *   <li>All elements on the context stack should be accessible.
   *   <li>Multiple sections per template should be permitted.
   *   <li>Failed context lookups should be considered falsey.
   *   <li>Dotted names should be valid for Section tags.
   *   <li>Dotted names that cannot be resolved should be considered falsey.
   *   <li>Dotted Names - Context Precedence: Dotted names should be resolved against former
   *       resolutions.
   * </ul>
   *
   * @param path The object path.
   * @return The value associated to the given key or <code>null</code> if no value is found.
   */
  public Object get(final List<PathExpression> path) {
    PathExpression head = path.get(0);
    if (head instanceof DataPath && "@partial-block".equals(head.toString()) && path.size() == 1) {
      return partialBlock();
    }
    boolean local = head.local();
    boolean dataLookup = head instanceof DataPath;
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
        value = dataLookup ? null : expr.eval(resolver, it, it.model);
        if (value == null) {
          // No luck, check the extended context.
          value = expr.eval(resolver, it.extendedContext, it.extendedContext.model);

          if (value == null) {
            // data context
            value = expr.eval(resolver, it, it.data);
          }
        }
        // a numeric context is a leaf scope, like in handlebars.js: names don't resolve upwards
        it = it.model instanceof Number ? null : it.parent;
      }
    }
    return value == NULL ? null : value;
  }

  /**
   * Find the first segment of the path that can't be resolved. Only plain property and data paths
   * are checked.
   *
   * @param path The object path.
   * @return The name of the undefined segment or <code>null</code> if the path is defined.
   */
  public String undefinedSegment(final List<PathExpression> path) {
    for (PathExpression expr : path) {
      if (!(expr instanceof PropertyPath || expr instanceof DataPath)) {
        return null;
      }
    }
    PathExpression head = path.get(0);
    Object current = get(path.subList(0, 1));
    if (current == null && !isDeclared(head.toString())) {
      return head.toString().replaceFirst("^@", "");
    }
    for (int i = 1; i < path.size() && current != null; i++) {
      String name = path.get(i).toString();
      Object value = resolver.resolve(current, name);
      if (value == null && !(current instanceof Map && ((Map<?, ?>) current).containsKey(name))) {
        return name;
      }
      current = value == NULL ? null : value;
    }
    return null;
  }

  private boolean isDeclared(final String name) {
    String bare = name.replaceFirst("^@", "");
    for (Context it = this; it != null; it = it.parent) {
      if (hasKey(it.model, name) || hasKey(it.extendedContext, name) || hasProperty(it, name)) {
        return true;
      }
      for (Context frame = it; frame != null; frame = frame.dataParent) {
        if (hasKey(frame.data, name) || hasKey(frame.data, bare)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasProperty(final Context it, final String name) {
    if (it.model == null || it.model instanceof Map) {
      return false;
    }
    return resolver.propertySet(it.model).stream().anyMatch(e -> e.getKey().equals(name));
  }

  private static boolean hasKey(final Object source, final String name) {
    Object map = source instanceof Context ? ((Context) source).model : source;
    return map instanceof Map && ((Map<?, ?>) map).containsKey(name);
  }

  /**
   * The partial block provided to the partial being rendered, if any.
   *
   * @return The partial block or null.
   */
  private Template partialBlock() {
    if (data == null) {
      return null;
    }
    LinkedList<Map<String, Template>> partials = data(INLINE_PARTIALS);
    if (partials == null || partials.isEmpty()) {
      return null;
    }
    Template block = partials.getLast().get("@partial-block");
    Template callee = data(CALLEE);
    if (block == null
        || callee == null
        || !callee.filename().equalsIgnoreCase(block.filename())
        || !Arrays.equals(callee.position(), block.position())) {
      return null;
    }
    return block;
  }

  /**
   * Lookup the given key inside the context stack.
   *
   * <ul>
   *   <li>Objects and hashes should be pushed onto the context stack.
   *   <li>All elements on the context stack should be accessible.
   *   <li>Multiple sections per template should be permitted.
   *   <li>Failed context lookups should be considered falsey.
   *   <li>Dotted names should be valid for Section tags.
   *   <li>Dotted names that cannot be resolved should be considered falsey.
   *   <li>Dotted Names - Context Precedence: Dotted names should be resolved against former
   *       resolutions.
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
   *
   * <ul>
   *   <li>Objects and hashes should be pushed onto the context stack.
   *   <li>All elements on the context stack should be accessible.
   *   <li>Multiple sections per template should be permitted.
   *   <li>Failed context lookups should be considered falsey.
   *   <li>Dotted names should be valid for Section tags.
   *   <li>Dotted names that cannot be resolved should be considered falsey.
   *   <li>Dotted Names - Context Precedence: Dotted names should be resolved against former
   *       resolutions.
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

  /** Destroy this context by cleaning up instance attributes. */
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
  public static Context newBlockParamContext(
      final Context parent, final List<String> names, final List<Object> values) {
    Map<String, Object> hash = new HashMap<>();
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
  public static Context newPartialContext(
      final Context ctx, final String scope, final Map<String, Object> hash) {
    return newPartialContext(ctx, ctx.get(scope), hash);
  }

  /**
   * Creates a partial context with an already resolved model.
   *
   * @param ctx The parent context.
   * @param scopedModel The partial model.
   * @param hash Partial arguments.
   * @return A new partial context.
   */
  public static Context newPartialContext(
      final Context ctx, final Object scopedModel, final Map<String, Object> hash) {
    var currentModel = ctx.model;
    // don't introduce a new scope when there is no need
    var parent = ctx.parent != null && currentModel == scopedModel ? ctx.parent : ctx;
    Map<String, Object> finalHash;
    if (ctx instanceof PartialCtx) {
      // propagate hash arguments down
      finalHash = new HashMap<>(((PartialCtx) ctx).hash);
      finalHash.putAll(hash);
    } else {
      finalHash = new HashMap<>(hash);
      if (ctx.extendedContext != null && ctx.extendedContext.model instanceof Map localVariables) {
        // propagate iteration variables down
        finalHash.putAll(localVariables);
      }
    }
    return new PartialCtx(parent, scopedModel, finalHash);
  }

  /**
   * Creates an isolated partial context: it has an empty model and can't see parent contexts, only
   * hash arguments.
   *
   * @param ctx The parent context.
   * @param hash Partial arguments.
   * @return A new partial context.
   */
  public static Context newIsolatedPartialContext(
      final Context ctx, final Map<String, Object> hash) {
    Context partial = newPartialContext(ctx, Collections.emptyMap(), hash);
    partial.parent = null;
    return partial;
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
    child.data = new HashMap<>();
    child.dataParent = this;
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
    ctx.dataParent = context.dataParent;
    ctx.resolver = context.resolver;
    return ctx;
  }
}
