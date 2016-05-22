package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public class TagTypeTest extends AbstractTest {

  Hash helpers = $("tag", new Helper<Object>() {
    @Override
    public Object apply(final Object context, final Options options) throws IOException {
      return options.tagType.name();
    }
  }, "vowels", new Helper<Object>() {
    @Override
    public Object apply(final Object context, final Options options) throws IOException {
      return options.helperName;
    }
  });

  @Test
  public void varTag() throws IOException {
    shouldCompileTo("{{tag}}", $, helpers, "VAR");
  }

  @Test
  public void unescapeVarTag() throws IOException {
    shouldCompileTo("{{&tag}}", $, helpers, "AMP_VAR");
  }

  @Test
  public void tripleVarTag() throws IOException {
    shouldCompileTo("{{{tag}}}", $, helpers, "TRIPLE_VAR");
  }

  @Test
  public void sectionTag() throws IOException {
    shouldCompileTo("{{#tag}}{{/tag}}", $, helpers, "SECTION");
  }

  @Test
  public void inline() {
    assertTrue(TagType.VAR.inline());

    assertTrue(TagType.AMP_VAR.inline());

    assertTrue(TagType.TRIPLE_VAR.inline());

    assertTrue(TagType.SUB_EXPRESSION.inline());
  }

  @Test
  public void block() {
    assertTrue(!TagType.SECTION.inline());
  }

  @Test
  public void collectVar() throws IOException {
    assertSetEquals(Arrays.asList("a", "z", "k"), compile("{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}")
        .collect(TagType.VAR));
  }

  @Test
  public void collectSubexpression() throws IOException {
    assertSetEquals(Arrays.asList("tag"), compile("{{vowels (tag)}}", helpers)
        .collect(TagType.SUB_EXPRESSION));
  }

  @Test
  public void collectAmpVar() throws IOException {
    assertSetEquals(Arrays.asList("b"), compile("{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}")
        .collect(TagType.AMP_VAR));
  }

  @Test
  public void collectTripleVar() throws IOException {
    assertSetEquals(Arrays.asList("tvar"),
        compile("{{{tvar}}}{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}")
            .collect(TagType.TRIPLE_VAR));
  }

  @Test
  public void collectSection() throws IOException {
    assertSetEquals(Arrays.asList("hello"), compile("{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}")
        .collect(TagType.SECTION));
  }

  @Test
  public void collectSectionWithSubExpression() throws IOException {
    assertSetEquals(Arrays.asList("tag"), compile("{{#hello}}{{vowels (tag)}}{{/hello}}", helpers)
        .collect(TagType.SUB_EXPRESSION));
  }

  @Test
  public void collectSectionAndVars() throws IOException {
    assertSetEquals(
        Arrays.asList("hello", "a", "b", "z", "k", "vowels", "tag"),
        compile("{{#hello}}{{a}}{{&b}}{{z}}{{/hello}}{{k}}{{vowels (tag)}}", helpers)
            .collect(TagType.SECTION, TagType.VAR, TagType.TRIPLE_VAR, TagType.AMP_VAR,
                TagType.SUB_EXPRESSION));
  }

  private void assertSetEquals(List<String> list, List<String> list2) {
    assertEquals(new HashSet<String>(list), new HashSet<String>(list2));
  }
}
