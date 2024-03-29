/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
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

  /** Not used. */
  private Files() {}

  /**
   * Read a file from a classpath location.
   *
   * @param location The classpath location.
   * @param charset Charset.
   * @return The file content.
   * @throws IOException If the file can't be read.
   */
  public static String read(final String location, final Charset charset) throws IOException {
    return read(Files.class.getResourceAsStream(location), charset);
  }

  /**
   * Read a file content.
   *
   * @param source The file.
   * @param charset Charset.
   * @return The file content.
   * @throws IOException If the file can't be read.
   */
  public static String read(final File source, final Charset charset) throws IOException {
    return read(new FileInputStream(source), charset);
  }

  /**
   * Read a file source.
   *
   * @param source The file source.
   * @param charset Charset.
   * @return The file content.
   * @throws IOException If the file can't be read.
   */
  public static String read(final InputStream source, final Charset charset) throws IOException {
    return read(new InputStreamReader(source, charset));
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
