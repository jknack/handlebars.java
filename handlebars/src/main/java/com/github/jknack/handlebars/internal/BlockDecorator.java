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
 * A block decorator implementation.
 *
 * @author edgar
 * @since 4.0.0
 */
public class BlockDecorator extends Block {

  /** Underlying decorator. */
  private Decorator decorator;

  /** True, if this is top level decorator. */
  private boolean root;

  /**
   * Creates a new {@link BlockDecorator}.
   *
   * @param handlebars The handlebars object.
   * @param name The section's name.
   * @param inverted True if it's inverted.
   * @param params The parameter list.
   * @param hash The hash.
   * @param blockParams The block param names.
   * @param root True, if this is top level decorator.
   */
  public BlockDecorator(final Handlebars handlebars, final String name, final boolean inverted,
      final List<Object> params, final Map<String, Object> hash, final List<String> blockParams,
      final boolean root) {
    super(handlebars, name, inverted, params, hash, blockParams);
    this.root = root;
    this.tagType = TagType.START_SECTION;
  }

  @Override
  protected void postInit() {
    this.decorator = handlebars.decorator(name);
  }

  @Override
  public void before(final Context context, final Writer writer) throws IOException {

    Context ctx = root ? Context.copy(context, null) : context;

    if (body != null) {
      ((BaseTemplate) body).before(ctx, writer);
    }

    Options options = new Options.Builder(handlebars, name, TagType.SECTION, ctx, body)
        .setInverse(Template.EMPTY)
        .setParams(decoParams(ctx))
        .setHash(hash(ctx))
        .setBlockParams(blockParams)
        .build();
    options.data(Context.PARAM_SIZE, this.params.size());

    decorator.apply(body, options);
  }

  @Override
  protected String suffix() {
    return "*";
  }

  @Override
  public void apply(final Context context, final Writer writer) throws IOException {
    // NOOP
  }

  @Override
  public void after(final Context context, final Writer writer) throws IOException {
    // NOOP
  }

}
