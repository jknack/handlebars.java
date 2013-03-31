/**
 * Copyright (c) 2012-2013 Edgar Espina
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

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Reader;

/**
 * A template source which forwards all its method calls to another template source..
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public class ForwardingTemplateSource extends AbstractTemplateSource {

  /**
   * The template source.
   */
  private final TemplateSource source;

  /**
   * Creates a new {@link ForwardingTemplateSource}.
   *
   * @param source The template source to forwards all the method calls.
   */
  public ForwardingTemplateSource(final TemplateSource source) {
    this.source = notNull(source, "The source is required.");
  }

  @Override
  public String content() throws IOException {
    return source.content();
  }

  @Override
  public Reader reader() throws IOException {
    return source.reader();
  }

  @Override
  public String filename() {
    return source.filename();
  }

  @Override
  public long lastModified() {
    return source.lastModified();
  }

}
