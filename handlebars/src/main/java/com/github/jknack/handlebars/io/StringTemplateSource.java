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
import java.io.StringReader;


/**
 * String implementation of {@link TemplateSource}.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public class StringTemplateSource implements TemplateSource {

  /**
   * The template's content. Required.
   */
  private final String content;

  /**
   * The template's file name. Required.
   */
  private final String filename;

  /**
   * The last modified date.
   */
  private final long lastModified;

  /**
   * Creates a new {@link StringTemplateSource}.
   *
   * @param filename The template's file name. Required.
   * @param content The template's content. Required.
   */
  public StringTemplateSource(final String filename, final String content) {
    this.content = notNull(content, "The content is required.");
    this.filename = notNull(filename, "The filename is required.");
    this.lastModified = content.hashCode();
  }

  @Override
  public String content() throws IOException {
    return content;
  }

  @Override
  public Reader reader() throws IOException {
    return new StringReader(content);
  }

  @Override
  public String filename() {
    return filename;
  }

  @Override
  public long lastModified() {
    return lastModified;
  }

  @Override
  public int hashCode() {
    return (int) lastModified;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof StringTemplateSource) {
      return content.equals(((StringTemplateSource) obj).content);
    }
    return false;
  }

  @Override
  public String toString() {
    return filename;
  }
}
