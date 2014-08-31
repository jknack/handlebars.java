package com.github.jknack.handlebars;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyPathParserTest {

  private PropertyPathParser parser = new PropertyPathParser("./");

  @Test
  public void testSinglePath() {
    assertArrayEquals(new String[]{"this"}, parser.parsePath("this"));
  }

  @Test
  public void testDotPath() {
    assertArrayEquals(array("this", "foo"), parser.parsePath("this.foo"));
    assertArrayEquals(array("this", "foo", "bar"), parser.parsePath("this.foo.bar"));
  }

  @Test
  public void testSlashPath() {
    assertArrayEquals(array("this", "foo"), parser.parsePath("this/foo"));
    assertArrayEquals(array("this", "foo", "bar", "baz"), parser.parsePath("this/foo/bar/baz"));
  }

  @Test
  public void testDotAndSlashPath() {
    assertArrayEquals(array("this", "foo"), parser.parsePath("this.foo"));
    assertArrayEquals(array("this", "foo", "bar", "baz"), parser.parsePath("this.foo/bar.baz"));
  }

  @Test
  public void testSingleLiteral() {
    assertArrayEquals(array("[foo]"), parser.parsePath("[foo]"));
    assertArrayEquals(array("[foo.bar.baz]"), parser.parsePath("[foo.bar.baz]"));
    assertArrayEquals(array("[foo/bar/baz]"), parser.parsePath("[foo/bar/baz]"));
    assertArrayEquals(array("[foo/bar.baz]"), parser.parsePath("[foo/bar.baz]"));
  }

  @Test
  public void testMultipleLiteralsAndJavaNames() {
    assertArrayEquals(array("this", "foo", "[bar.1]"), parser.parsePath("this.foo.[bar.1]"));
    assertArrayEquals(array("this", "_foo1", "[bar.1]", "_foo2", "[bar.2]"),
      parser.parsePath("this._foo1.[bar.1]._foo2.[bar.2]"));
  }

  private String[] array(String... vals) {
    return vals;
  }

}
