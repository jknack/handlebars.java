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
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Read file content utilities method.
 *
 * @author edgar.espina
 * @since 1.1.0
 */
public final class Files {

  /**
   * Not used.
   */
  private Files() {
  }

  /**
   * Read a file from a classpath location.
   *
   * @param location The classpath location.
   * @return The file content.
   * @throws IOException If the file can't be read.
   */
  public static String read(final String location) throws IOException {
    return read(Files.class.getResourceAsStream(location));
  }

  /**
   * Read a file content.
   *
   * @param source The file.
   * @return The file content.
   * @throws IOException If the file can't be read.
   */
  public static String read(final File source) throws IOException {
    return read(new FileInputStream(source));
  }

  /**
   * Read a file source.
   *
   * @param source The file source.
   * @return The file content.
   * @throws IOException If the file can't be read.
   */
  public static String read(final InputStream source) throws IOException {
    return read(new InputStreamReader(source, Charset.forName("UTF-8")));
  }

  /**
   * Read a file source.
   *
   * @param source The file source.
   * @return The file content.
   * @throws IOException If the file can't be read.
   */
  public static String read(final Reader source) throws IOException {
    return read(new BufferedReader(source));
  }

  /**
   * Read a file source.
   *
   * @param source The file source.
   * @return The file content.
   * @throws IOException If the file can't be read.
   */
  public static String read(final BufferedReader source) throws IOException {
    notNull(source, "The input is required.");
    try {
      int ch = source.read();
      StringBuilder script = new StringBuilder();
      while (ch != -1) {
        script.append((char) ch);
        ch = source.read();
      }
      return script.toString();
    } finally {
      source.close();
    }
  }
}
