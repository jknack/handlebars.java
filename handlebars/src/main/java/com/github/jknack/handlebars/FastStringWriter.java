/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;
import java.io.Writer;

/**
 * A string writer without locking.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FastStringWriter extends Writer {

  /**
   * Default buffer size for top-level templates. Set it with the <code>hbs.buffer</code> system
   * property. Default is: 1600.
   */
  public static final int BUFFER_SIZE = bufferSize("hbs.buffer", 1600);

  /**
   * Default buffer size for block bodies (<code>options.fn</code>, <code>options.inverse</code>).
   * Set it with the <code>hbs.buffer.block</code> system property. Default is: 128.
   */
  public static final int BLOCK_BUFFER_SIZE = bufferSize("hbs.buffer.block", 128);

  /**
   * Default buffer size for sub-expressions. Set it with the <code>hbs.buffer.subexpression</code>
   * system property. Default is: 32.
   */
  public static final int SUBEXPRESSION_BUFFER_SIZE = bufferSize("hbs.buffer.subexpression", 32);

  /** The internal buffer. */
  private StringBuilder buffer;

  /** Creates a new writer using the default {@link #BUFFER_SIZE}. */
  public FastStringWriter() {
    this(BUFFER_SIZE);
  }

  /**
   * Creates a new writer.
   *
   * @param capacity Initial buffer capacity (must be positive).
   */
  public FastStringWriter(final int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be positive: " + capacity);
    }
    this.buffer = new StringBuilder(capacity);
  }

  /**
   * Read a buffer size from a system property.
   *
   * @param property Property name.
   * @param defaultValue Default value.
   * @return Buffer size.
   */
  private static int bufferSize(final String property, final int defaultValue) {
    int size = Integer.parseInt(System.getProperty(property, String.valueOf(defaultValue)));
    if (size <= 0) {
      throw new IllegalArgumentException(property + " must be positive: " + size);
    }
    return size;
  }

  @Override
  public Writer append(final char c) throws IOException {
    buffer.append(c);
    return this;
  }

  @Override
  public Writer append(final CharSequence csq) throws IOException {
    buffer.append(csq);
    return this;
  }

  @Override
  public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
    buffer.append(csq, start, end);
    return this;
  }

  @Override
  public void write(final char[] buffer) throws IOException {
    this.buffer.append(buffer);
  }

  @Override
  public void write(final int c) throws IOException {
    this.buffer.append((char) c);
  }

  @Override
  public void write(final String str) throws IOException {
    this.buffer.append(str);
  }

  @Override
  public void write(final String str, final int off, final int len) throws IOException {
    buffer.append(str, off, len);
  }

  @Override
  public void write(final char[] buffer, final int off, final int len) throws IOException {
    if (off < 0 || off > buffer.length || len < 0 || off + len > buffer.length || off + len < 0) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return;
    }
    this.buffer.append(buffer, off, len);
  }

  @Override
  public void flush() throws IOException {}

  @Override
  public void close() throws IOException {
    buffer = null;
  }

  @Override
  public String toString() {
    return buffer.toString();
  }
}
