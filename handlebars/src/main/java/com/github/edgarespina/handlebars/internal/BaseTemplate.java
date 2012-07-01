package com.github.edgarespina.handlebars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlebars.Context;
import com.github.edgarespina.handlebars.HandlebarsException;
import com.github.edgarespina.handlebars.Template;

/**
 * Base class for {@link Template}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
abstract class BaseTemplate implements Template {

  /**
   * The line of this template.
   */
  protected int line;

  /**
   * The column of this template.
   */
  protected int column;

  /**
   * The file's name.
   */
  protected String filename;

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
    return apply(wrap(context));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void apply(final Object context, final Writer writer)
      throws IOException {
    apply(wrap(context), writer);
  }

  @Override
  public CharSequence apply(final Context context) throws IOException {
    FastStringWriter writer = new FastStringWriter();
    apply(context, writer);
    return writer.toString();
  }

  @Override
  public void apply(final Context context, final Writer writer)
      throws IOException {
    checkNotNull(writer, "A writer is required.");
    try {
      merge(wrap(context), writer);
    } catch (HandlebarsException ex) {
      throw ex;
    } catch (Exception ex) {
      String message =
          filename + ":" + line + ":" + column + ": "
              + ex.getMessage() + "\n";
      message += "    " + toString();
      HandlebarsException hex = new HandlebarsException(message);
      // Override the stack-trace
      hex.setStackTrace(ex.getStackTrace());
      throw hex;
    }
  }

  /**
   * Wrap the candidate object as a Context, or creates a new context.
   *
   * @param candidate The candidate object.
   * @return A context.
   */
  private static Context wrap(final Object candidate) {
    if (candidate instanceof Context) {
      return (Context) candidate;
    }
    return Context.newContext(candidate);
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
  public String toString() {
    return text();
  }

  /**
   * Set the file's name.
   *
   * @param filename The file's name.
   * @return This template.
   */
  public BaseTemplate filename(final String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Set the template position.
   *
   * @param line The line.
   * @param column The column.
   * @return This template.
   */
  public BaseTemplate position(final int line, final int column) {
    this.line = line;
    this.column = column;
    return this;
  }
}
