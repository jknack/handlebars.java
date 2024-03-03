/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import com.github.jknack.handlebars.io.TemplateSource;

/**
 * The Handlebars Parser.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
public interface Parser {

  /**
   * Parse a handlebars input and return a {@link Template}.
   *
   * @param source The input to parse. Required.
   * @return A new handlebars template.
   * @throws IOException If the resource cannot be loaded.
   */
  Template parse(TemplateSource source) throws IOException;
}
