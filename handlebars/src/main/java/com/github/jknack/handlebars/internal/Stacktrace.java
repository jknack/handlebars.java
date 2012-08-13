/**
 * Copyright (c) 2012 Edgar Espina
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
package com.github.jknack.handlebars.internal;

/**
 * Utitlity class for tracking template stack.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
class Stacktrace {

  /**
   * The line number.
   */
  private final int line;

  /**
   * The column number.
   */
  private final int column;

  /**
   * The file's name.
   */
  private final String filename;

  /**
   * Creates a new {@link Stacktrace}.
   *
   * @param line The line number.
   * @param column The column number.
   * @param filename The file's name.
   */
  public Stacktrace(final int line, final int column,
      final String filename) {
    this.line = line;
    this.column = column;
    this.filename = filename;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + column;
    result = prime * result + (filename == null ? 0 : filename.hashCode());
    result = prime * result + line;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Stacktrace other = (Stacktrace) obj;
    if (column != other.column) {
      return false;
    }
    if (filename == null) {
      if (other.filename != null) {
        return false;
      }
    } else if (!filename.equals(other.filename)) {
      return false;
    }
    if (line != other.line) {
      return false;
    }
    return true;
  }

  /**
   * The file's name.
   *
   * @return The file's name.
   */
  public String getFilename() {
    return filename;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer
        .append("at ").append(filename).append(":")
        .append(line).append(":").append(column);
    return buffer.toString();
  }
}
