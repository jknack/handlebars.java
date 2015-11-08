package com.github.jknack.handlebars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Decorator;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

/**
 * Var decorator.
 *
 * @author edgar
 * @since 4.0.0
 */
public class VarDecorator extends Variable {

  /** True, if this is top level decorator. */
  private boolean root;

  /** Underlying decorator. */
  private Decorator decorator;

  /**
   * Creates a new {@link VarDecorator}.
   *
   * @param handlebars The handlebars instance.
   * @param name The variable's name. Required.
   * @param type The variable's type. Required.
   * @param params The variable's parameters. Required.
   * @param hash The variable's hash. Required.
   * @param root True, if this is top level decorator.
   */
  public VarDecorator(final Handlebars handlebars, final String name, final TagType type,
      final List<Object> params, final Map<String, Object> hash, final boolean root) {
    super(handlebars, name, type, params, hash);
    this.root = root;
  }

  @Override
  protected void postInit() {
    this.decorator = handlebars.decorator(name);
  }

  @Override
  public void before(final Context context, final Writer writer) throws IOException {

    Context ctx = root ? Context.copy(context, null) : context;

    Options options = new Options.Builder(handlebars, name, type, ctx, this)
        .setInverse(Template.EMPTY)
        .setParams(decoParams(ctx))
        .setHash(hash(ctx))
        .build();
    options.data(Context.PARAM_SIZE, this.params.size());

    decorator.apply(this, options);
  }

  @Override
  public void apply(final Context context, final Writer writer) throws IOException {
    // NOOP
  }

  @Override
  protected String suffix() {
    return "*";
  }

}
