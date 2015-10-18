package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PropertyPathParserTest {

  private PropertyPathParser parser = new PropertyPathParser("./");

  @Test
  public void testSinglePath() {
    assertEquals(Arrays.asList("this"), parser.parsePath("this"));
  }

  @Test
  public void testDotPath() {
    assertEquals(array("this", "foo"), parser.parsePath("this.foo"));
    assertEquals(array("this", "foo", "bar"), parser.parsePath("this.foo.bar"));
  }

  @Test
  public void testSlashPath() {
    assertEquals(array("this", "foo"), parser.parsePath("this/foo"));
    assertEquals(array("this", "foo", "bar", "baz"), parser.parsePath("this/foo/bar/baz"));
  }

  @Test
  public void testDotAndSlashPath() {
    assertEquals(array("this", "foo"), parser.parsePath("this.foo"));
    assertEquals(array("this", "foo", "bar", "baz"), parser.parsePath("this.foo/bar.baz"));
  }

  @Test
  public void testSingleLiteral() {
    assertEquals(array("[foo]"), parser.parsePath("[foo]"));
    assertEquals(array("[foo.bar.baz]"), parser.parsePath("[foo.bar.baz]"));
    assertEquals(array("[foo/bar/baz]"), parser.parsePath("[foo/bar/baz]"));
    assertEquals(array("[foo/bar.baz]"), parser.parsePath("[foo/bar.baz]"));
  }

  @Test
  public void testMultipleLiteralsAndJavaNames() {
    assertEquals(array("this", "foo", "[bar.1]"), parser.parsePath("this.foo.[bar.1]"));
    assertEquals(array("this", "_foo1", "[bar.1]", "_foo2", "[bar.2]"),
      parser.parsePath("this._foo1.[bar.1]._foo2.[bar.2]"));
  }

  private List<String> array(final String... vals) {
    return Arrays.asList(vals);
  }

}
