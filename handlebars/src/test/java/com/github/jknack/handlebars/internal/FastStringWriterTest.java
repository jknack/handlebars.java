/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class FastStringWriterTest {

  @Test
  public void writeCharArray() throws IOException {
    Writer writer = new FastStringWriter();
    writer.write(new char[] {'a', 'b', 'c'});
    assertEquals("abc", writer.toString());
    IOUtils.closeQuietly(writer);
  }

  @Test
  public void writeInt() throws IOException {
    Writer writer = new FastStringWriter();
    writer.write(55);
    assertEquals("7", writer.toString());
    IOUtils.closeQuietly(writer);
  }

  @Test
  public void writeString() throws IOException {
    Writer writer = new FastStringWriter();
    writer.write("7");
    assertEquals("7", writer.toString());
    IOUtils.closeQuietly(writer);
  }

  @Test
  public void writeStringWithOffsetAndLength() throws IOException {
    Writer writer = new FastStringWriter();
    writer.write("hello", 1, 4);
    assertEquals("ell", writer.toString());
    IOUtils.closeQuietly(writer);
  }

  @Test
  public void writeCharArrayWithOffsetAndLength() throws IOException {
    Writer writer = new FastStringWriter();
    writer.write(new char[] {'h', 'e', 'l', 'l', 'o'}, 1, 3);
    assertEquals("ell", writer.toString());
    IOUtils.closeQuietly(writer);
  }

  @Test
  public void writeCharArrayWithBadOffsetAndLength() throws IOException {
    assertThrows(
        IndexOutOfBoundsException.class,
        () -> {
          Writer writer = new FastStringWriter();
          writer.write(new char[] {'h', 'e', 'l', 'l', 'o'}, -1, 3);
          IOUtils.closeQuietly(writer);
        });
  }

  @Test
  public void writeCharArrayWithOffsetAndZeroLength() throws IOException {
    Writer writer = new FastStringWriter();
    writer.write(new char[] {'h', 'e', 'l', 'l', 'o'}, 1, 0);
    assertEquals("", writer.toString());
    IOUtils.closeQuietly(writer);
  }

  @Test
  public void flush() throws IOException {
    Writer writer = new FastStringWriter();
    writer.flush();
    IOUtils.closeQuietly(writer);
  }

  @Test
  public void close() throws IOException {
    Writer writer = new FastStringWriter();
    writer.append("hello");
    assertEquals("hello", writer.toString());
    writer.close();
  }
}
