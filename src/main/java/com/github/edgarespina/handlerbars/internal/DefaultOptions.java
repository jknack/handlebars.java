package com.github.edgarespina.handlerbars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Options;
import com.github.edgarespina.handlerbars.Template;

/**
 * An implementation of {@link Options}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class DefaultOptions implements Options {

  /**
   * An empty template implementation.
   */
  private static Template EMPTY = new Template() {
    @Override
    public String rawText() {
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
   * The current template. Required.
   */
  private Template template;

  /**
   * The current inverse template. Required.
   */
  private Template inverse;

  /**
   * The parameters.
   */
  private Object[] params;

  /**
   * The hash options.
   */
  private Map<String, Object> hash;

  /**
   * Creates a new {@link DefaultOptions}.
   *
   * @param template The current template. Required.
   * @param inverse The current inverse template. Optional.
   * @param context The current context. Required.
   * @param params The parameters. Required.
   * @param hash The hash. Required.
   */
  public DefaultOptions(final Template template,
      final Template inverse, final Context context, final Object[] params,
      final Map<String, Object> hash) {
    this.template = checkNotNull(template, "The template is required");
    this.inverse = inverse == null ? EMPTY : inverse;
    this.context = checkNotNull(context, "The context is required");
    this.params = checkNotNull(params, "The parameters are required");
    this.hash = checkNotNull(hash, "The hash are required");
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
    return apply(inverse, context);
  }

  /**
   * Apply the given template (if possible) to the given context.
   *
   * @param template The template.
   * @param context The context.
   * @return The resulting text.
   * @throws IOException If a resource cannot be loaded.
   */
  private String apply(final Template template, final Object context)
      throws IOException {
    if (context == null) {
      return "";
    }
    String result =
        template.apply(context == this.context ?
            this.context :
            Context.scope(this.context, context)
            );
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
  public boolean isEmpty(final Object value) {
    return Handlebars.Utils.isEmpty(value);
  }

  /**
   * Cleanup resources.
   */
  public void destroy() {
    this.hash.clear();
    this.hash = null;
    this.params = null;
    this.context = null;
    this.template = null;
    this.inverse = null;
  }
}
