package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.io.Writer;

public abstract class Template {

  private static class FastStringWriter extends Writer {
    private final StringBuilder buffer = new StringBuilder();

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
    public void write(final String str, final int off, final int len)
        throws IOException {
      buffer.append(str, off, off + len);
    }

    @Override
    public void write(final char[] buffer, final int off, final int len)
        throws IOException {
      if (off < 0 || off > buffer.length || len < 0 ||
          off + len > buffer.length || off + len < 0) {
        throw new IndexOutOfBoundsException();
      } else if (len == 0) {
        return;
      }
      this.buffer.append(buffer, off, len);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String toString() {
      return buffer.toString();
    }

  }

  public abstract void merge(Scope scope, Writer writer)
      throws IOException;

  public final String merge(final Scope scope) throws IOException {
    FastStringWriter writer = new FastStringWriter();
    merge(scope, writer);
    return writer.toString();
  }

  @Override
  public abstract String toString();
}
