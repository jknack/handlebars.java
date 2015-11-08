/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.io;

import java.io.IOException;

/**
 * The template source. Implementation of {@link TemplateSource} must implement
 * {@link #equals(Object)} and {@link #hashCode()} methods. This two methods are the core of the
 * cache system.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public interface TemplateSource {

  /**
   * The template content.
   *
   * @return The template content.
   * @throws IOException If the template can't read.
   */
  String content() throws IOException;

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
