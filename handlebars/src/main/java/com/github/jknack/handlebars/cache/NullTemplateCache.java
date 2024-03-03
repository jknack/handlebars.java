/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.cache;

import java.io.IOException;

import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Null cache implementation.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public enum NullTemplateCache implements TemplateCache {

  /** Shared instance of null cache. */
  INSTANCE;

  @Override
  public void clear() {}

  @Override
  public void evict(final TemplateSource source) {}

  @Override
  public NullTemplateCache setReload(final boolean reload) {
    return this;
  }

  @Override
  public Template get(final TemplateSource source, final Parser parser) throws IOException {
    return parser.parse(source);
  }
}
