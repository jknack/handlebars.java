package com.github.edgarespina.handlerbars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlerbars.Template;

/**
 * Partials begin with a greater than sign, like {{> box}}.
 * Partials are rendered at runtime (as opposed to compile time), so recursive
 * partials are possible. Just avoid infinite loops.
 * They also inherit the calling context.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Partial extends BaseTemplate {

  /**
   * The internal template.
   */
  private Template template;

  /**
   * Set the partial template.
   *
   * @param template The template. Required.
   * @return This partial.
   */
  public Partial template(final Template template) {
    this.template = checkNotNull(template, "The template is required.");
    return this;
  }

  @Override
  public void doApply(final Context scope, final Writer writer)
      throws IOException {
    template.apply(scope, writer);
  }

  @Override
  public String rawText() {
    return template.toString();
  }

  @Override
  public boolean remove(final Template child) {
    return ((BaseTemplate) template).remove(child);
  }
}
