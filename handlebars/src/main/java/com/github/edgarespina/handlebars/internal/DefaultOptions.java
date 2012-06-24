package com.github.edgarespina.handlebars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.github.edgarespina.handlebars.Context;
import com.github.edgarespina.handlebars.ContextFactory;
import com.github.edgarespina.handlebars.Handlebars;
import com.github.edgarespina.handlebars.Options;
import com.github.edgarespina.handlebars.Template;

/**
 * An implementation of {@link Options}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class DefaultOptions extends Options {

  /**
   * An empty template implementation.
   */
  private static Template EMPTY = new Template() {
    @Override
    public String text() {
      return "";
    }

    @Override
    public String apply(final Object context) throws IOException {
      return "";
    }

    @Override
    public void apply(final Object context, final Writer writer)
        throws IOException {
    }
  };

  /**
   * The current context. Required.
   */
  private Context context;

  /**
   * A thread safe storage.
   */
  private Map<String, Object> storage;

  /**
   * Creates a new {@link DefaultOptions}.
   *
   * @param handlebars The {@link Handlebars} object. Required.
   * @param fn The current template. Required.
   * @param inverse The current inverse template. Optional.
   * @param context The current context. Required.
   * @param params The parameters. Required.
   * @param hash The hash. Required.
   */
  public DefaultOptions(final Handlebars handlebars, final Template fn,
      final Template inverse, final Context context, final Object[] params,
      final Map<String, Object> hash) {
    super(handlebars, fn, inverse == null ? EMPTY : inverse, params, hash);
    this.context = checkNotNull(context, "The context is required");
    this.storage = context.storage();
  }

  @Override
  public CharSequence fn() throws IOException {
    return fn(context);
  }

  @Override
  public CharSequence fn(final Object context) throws IOException {
    return applyIfPossible(fn, context);
  }

  @Override
  public CharSequence inverse() throws IOException {
    return inverse(context);
  }

  @Override
  public CharSequence inverse(final Object context) throws IOException {
    return applyIfPossible(inverse, context);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(final String name) {
    T value = (T) context.get(name);
    if (value == null) {
      value = (T) storage.get(name);
    }
    return value;
  }

  @Override
  public Template partial(final String path) {
    return partials().get(path);
  }

  @Override
  public void partial(final String path, final Template partial) {
    partials().put(path, partial);
  }

  /**
   * Apply the given template if the context object isn't null.
   *
   * @param template The template.
   * @param context The context object.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  private CharSequence applyIfPossible(final Template template,
      final Object context)
      throws IOException {
    if (context == null) {
      return "";
    }
    return apply(template, context);
  }

  @Override
  public CharSequence apply(final Template template) throws IOException {
    return apply(template, this.context);
  }

  @Override
  public CharSequence apply(final Template template, final Object context)
      throws IOException {
    CharSequence result =
        template.apply(context == this.context
            ? context
            : ContextFactory.wrap(this.context, context));
    return result;
  }

  /**
   * Return the partials storage.
   *
   * @return The partials storage.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Template> partials() {
    return (Map<String, Template>) storage.get("partials");
  }

  /**
   * Cleanup resources.
   */
  public void destroy() {
    this.hash.clear();
    this.context = null;
    this.storage = null;
  }

}
