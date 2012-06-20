package com.github.edgarespina.handlerbars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Template;

/**
 * Base class for {@link Template}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
abstract class BaseTemplate implements Template {

  /**
   * Remove the child template.
   *
   * @param child The template to be removed.
   * @return True, if the child was removed
   */
  public abstract boolean remove(Template child);

  /**
   * {@inheritDoc}
   */
  @Override
  public final CharSequence apply(final Object context) throws IOException {
    FastStringWriter writer = new FastStringWriter();
    apply(context, writer);
    return writer.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void apply(final Object context, final Writer writer)
      throws IOException {
    checkNotNull(writer, "A writer is required.");
    merge(DefaultContext.wrap(context), writer);
  }

  /**
   * Merge a child template into the writer.
   *
   * @param context The scope object.
   * @param writer The writer.
   * @throws IOException If a resource cannot be loaded.
   */
  protected abstract void merge(final Context context, Writer writer)
      throws IOException;

  @Override
  public final String toString() {
    return text();
  }
}
