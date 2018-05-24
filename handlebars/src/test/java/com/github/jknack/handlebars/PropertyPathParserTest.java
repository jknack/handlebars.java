package com.github.jknack.handlebars;

import static org.apache.commons.lang3.StringUtils.join;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PropertyPathParserTest {

  @Test
  public void testSinglePath() {
    eq(Arrays.asList("this"), PathCompiler.compile("this"));
  }

  @Test
  public void testDotPath() {
    eq(array("this", "foo"), PathCompiler.compile("this.foo"));
    eq(array("this", "foo", "bar"), PathCompiler.compile("this.foo.bar"));
  }

  @Test
  public void testSlashPath() {
    eq(array("this", "foo"), PathCompiler.compile("this/foo"));
    eq(array("this", "foo", "bar", "baz"), PathCompiler.compile("this/foo/bar/baz"));
  }

  @Test
  public void testDotAndSlashPath() {
    eq(array("this", "foo"), PathCompiler.compile("this.foo"));
    eq(array("this", "foo", "bar", "baz"), PathCompiler.compile("this.foo/bar.baz"));
  }

  @Test
  public void testSingleLiteral() {
    eq(array("foo"), PathCompiler.compile("[foo]"));
    eq(array("foo.bar.baz"), PathCompiler.compile("[foo.bar.baz]"));
    eq(array("foo/bar/baz"), PathCompiler.compile("[foo/bar/baz]"));
    eq(array("foo/bar.baz"), PathCompiler.compile("[foo/bar.baz]"));
    eq(array("/foo/bar.baz"), PathCompiler.compile("[/foo/bar.baz]"));
    eq(array("../foo/bar.baz"), PathCompiler.compile("[../foo/bar.baz]"));
    eq(array("./foo/bar.baz"), PathCompiler.compile("[./foo/bar.baz]"));
    eq(array("../"), PathCompiler.compile("[../]"));
    eq(array("./"), PathCompiler.compile("[./]"));
  }

  @Test
  public void testMultipleLiteralsAndJavaNames() {
    eq(array("this", "foo", "bar.1"), PathCompiler.compile("this.foo.[bar.1]"));
    eq(array("this", "_foo1", "bar.1", "_foo2", "bar.2"),
      PathCompiler.compile("this._foo1.[bar.1]._foo2.[bar.2]"));
  }

  private List<String> array(final String... vals) {
    return Arrays.asList(vals);
  }

  private void eq(final List<String> expected, final List<PathExpression> path) {
    assertEquals(join(expected, ", "), join(path, ", "));
  }

}
