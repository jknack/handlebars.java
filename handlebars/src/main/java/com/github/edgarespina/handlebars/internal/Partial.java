package com.github.edgarespina.handlebars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;

import com.github.edgarespina.handlebars.Context;
import com.github.edgarespina.handlebars.Template;

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
   * The partial path.
   */
  private String path;

  /**
   * Set the partial template.
   *
   * @param path The partial path.
   * @param template The template. Required.
   * @return This partial.
   */
  public Partial template(final String path, final Template template) {
    this.path = checkNotNull(path, "The path is required.");
    this.template = checkNotNull(template, "The template is required.");
    return this;
  }

  @Override
  public void merge(final Context scope, final Writer writer)
      throws IOException {
    template.apply(scope, writer);
  }

  @Override
  public String text() {
    return "{{>" + path + "}}";
  }

  @Override
  public boolean remove(final Template child) {
    return ((BaseTemplate) template).remove(child);
  }
}
