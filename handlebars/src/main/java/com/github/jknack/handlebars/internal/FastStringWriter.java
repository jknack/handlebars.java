/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;
import java.io.Writer;

/**
 * A string writer without locking.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class FastStringWriter extends Writer {

  /** buffer size for StrBuilder. */
  private static final int BUFFER_SIZE;

  static {
    BUFFER_SIZE = Integer.parseInt(System.getProperty("hbs.buffer", "1600"));
  }

  /** The internal buffer. */
  private StringBuilder buffer = new StringBuilder(BUFFER_SIZE);

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
