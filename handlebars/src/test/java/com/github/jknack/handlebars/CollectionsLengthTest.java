package com.github.jknack.handlebars;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
