package com.github.jknack.handlebars;

import java.io.IOException;

/**
 * A decorator allows a declarative means to both annotate particular blocks with metadata as
 * well as to wrap in behaviors when desired, prior to execution.
 *
 * @author edgar
 * @since 4.0.0
 */
public interface Decorator {

  /**
   * Decorate a template with metadata.
   *
   * @param fn Decorated template.
   * @param options Options object.
   * @throws IOException If something goes wrong.
   */
  void apply(Template fn, Options options) throws IOException;

}
