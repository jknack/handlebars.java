/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

/**
 * Unit test for {@link Template}.
 *
 * @author omede.firouz
 * @since 1.3.1
 */
public class TemplateTest extends AbstractTest {

  @Test
  public void simpleVarTests() throws IOException {
    assertVariables("{{v1}}", Arrays.asList("v1"));
    assertVariables("{{{v1}}}", Arrays.asList("v1"));
    assertVariables("{{&v1}}", Arrays.asList("v1"));
  }

  @Test
  public void sectionVarTest() throws IOException {
    assertVariables("{{#v1}}{{/v1}}", Arrays.asList("v1"));
    assertVariables("{{^v1}}{{/v1}}", Arrays.asList("v1"));
  }

  @Test
  public void handlerVarTest() throws IOException {
    assertVariables("{{#each v1}}{{/each}}", Arrays.asList("v1"));
    assertVariables("{{#if v1 'test'}}{{else}}{{/if}}", Arrays.asList("v1"));
    assertVariables("{{#unless v1}}{{/unless}}", Arrays.asList("v1"));
    assertVariables("{{#with v1}}{{/with}}", Arrays.asList("v1"));
  }

  @Test
  public void subExpressionVarTest() throws IOException {
    assertVariables("{{i18n (i18n v1)}}", Arrays.asList("v1"));
  }

  @Test
  public void hashVarTest() throws IOException {
    assertVariables("{{i18n locale=v1}}", Arrays.asList("v1"));
  }

  @Test
  public void nestedVarTest() throws IOException {
    assertVariables(
        "{{#if v1}}{{i18n locale=v4}}{{v2}}{{else}}{{#each"
            + " v5}}{{#v6}}{{^v7}}{{v3}}{{/v7}}{{/v6}}{{/each}}{{/if}}",
        Arrays.asList("v1", "v2", "v3", "v4", "v5", "v6", "v7"));
  }

  private void assertVariables(String input, List<String> expected) throws IOException {
    Handlebars hb = newHandlebars();
    Template template = hb.compileInline(input);

    Set<String> variableNames = new HashSet<>(template.collectReferenceParameters());

    List<String> tagNames = template.collect(TagType.values());
    for (String tagName : tagNames) {
      if (hb.helper(tagName) == null) {
        variableNames.add(tagName);
      }
    }

    assertEquals(new HashSet<>(expected), variableNames);
  }
}
