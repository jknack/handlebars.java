/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CollectionsLengthTest extends AbstractTest {

  public static class SizeAndLength {
    int size;
    int length;

    public int getSize() {
      return size;
    }

    public void setSize(int size) {
      this.size = size;
    }

    public int getLength() {
      return length;
    }

    public void setLength(int length) {
      this.length = length;
    }
  }

  @Test
  public void collectionLengthTest() throws IOException {
    List<String> list = new ArrayList<>();
    list.add("a");
    shouldCompileTo("{{this.length}}", list, "1");
  }

  @Test
  public void otherClassSizeAndLength() throws IOException {
    SizeAndLength sizeAndLength = new SizeAndLength();
    sizeAndLength.length = 5;
    sizeAndLength.size = 4;
    shouldCompileTo("{{this.length}}", sizeAndLength, "5");
    shouldCompileTo("{{this.size}}", sizeAndLength, "4");
  }
}
