/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * The template source. Implementation of {@link TemplateSource} must implement <code>equals(Object)
 * </code> and <code>hashCode()</code> methods. This two methods are the core of the cache system.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public interface TemplateSource {

  /**
   * The template content.
   *
   * @param charset Charset to use.
   * @return The template content.
   * @throws IOException If the template can't read.
   */
  String content(Charset charset) throws IOException;

  /**
   * The file's name.
   *
   * @return The file's name.
   */
  String filename();

  /**
   * The last modified date.
   *
   * @return The last modified date.
   */
  long lastModified();
}
