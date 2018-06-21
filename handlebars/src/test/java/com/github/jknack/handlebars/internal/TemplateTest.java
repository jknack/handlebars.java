/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.*;

import org.junit.Test;

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
    assertVariables("{{#if v1}}{{i18n locale=v4}}{{v2}}{{else}}{{#each v5}}{{#v6}}{{^v7}}{{v3}}{{/v7}}{{/v6}}{{/each}}{{/if}}",
        Arrays.asList("v1", "v2", "v3", "v4", "v5", "v6", "v7"));
  }

  @Test
  public void collectWithParamsTest() throws IOException {
    List<TagWithParams> tagsWithParams = getTagsWithParameters("{{i18n \"foo\"}}");
    assertEquals(tagsWithParams.size(), 1);
    TagWithParams tagWithParams = tagsWithParams.get(0);
    assertEquals(tagWithParams.getTagType(), TagType.VAR);
    assertEquals(tagWithParams.getTag(), "i18n");
    assertEquals(tagWithParams.getParams().size(), 1);
    Param param = tagWithParams.getParams().get(0);
    StrParam strParam = (StrParam) param;
    assertEquals(strParam.apply(null), "foo");
  }

  @Test
  public void collectWithParamsForEachTest() throws IOException {
    List<TagWithParams> tagsWithParams = getTagsWithParameters("{{#each shoppingCartItems as |item|}}{{i18n item}}{{/each}}");
    assertEquals(tagsWithParams.size(), 2);

    TagWithParams i18nTagWithParams = tagsWithParams.get(0);
    System.out.println("i18nTagWithParams.getParams() is "+ i18nTagWithParams.getParams());
    assertEquals(i18nTagWithParams.getTagType(), TagType.VAR);
    assertEquals(i18nTagWithParams.getTag(), "i18n");
    assertEquals(i18nTagWithParams.getParams().size(), 1);
    Param i18nParam = i18nTagWithParams.getParams().get(0);
    RefParam i18nRefParam = (RefParam) i18nParam;
    assertEquals(i18nRefParam.toString(), "item");

    TagWithParams eachTagWithParams = tagsWithParams.get(1);
    assertEquals(eachTagWithParams.getTagType(), TagType.SECTION);
    assertEquals(eachTagWithParams.getTag(), "each");
    assertEquals(eachTagWithParams.getParams().size(), 2);
    System.out.println("eachTagWithParams.getParams() is "+ eachTagWithParams.getParams());
    Param param1 = eachTagWithParams.getParams().get(0);
    RefParam refParam1 = (RefParam) param1;
    assertEquals(refParam1.toString(), "item");
    Param param2 = eachTagWithParams.getParams().get(1);
    RefParam refParam2 = (RefParam) param2;
    assertEquals(refParam2.toString(), "shoppingCartItems");

  }

  @Test
  public void collectWithParamsMultipleParamsTest() throws IOException {
    List<TagWithParams> tagsWithParams = getTagsWithParameters("{{embedded foo bar baz}}{{i18n \"value1\"}}");
    assertEquals(tagsWithParams.size(), 2);

    TagWithParams embeddedTagWithParams = tagsWithParams.get(0);
    assertEquals(embeddedTagWithParams.getTagType(), TagType.VAR);
    assertEquals(embeddedTagWithParams.getTag(), "embedded");
    assertEquals(embeddedTagWithParams.getParams().size(), 3);

    Param param1 = embeddedTagWithParams.getParams().get(0);
    RefParam refParam1 = (RefParam) param1;
    assertEquals(refParam1.toString(), "foo");

    Param param2 = embeddedTagWithParams.getParams().get(1);
    RefParam refParam2 = (RefParam) param2;
    assertEquals(refParam2.toString(), "bar");

    Param param3 = embeddedTagWithParams.getParams().get(2);
    RefParam refParam3 = (RefParam) param3;
    assertEquals(refParam3.toString(), "baz");

    TagWithParams i18nTagWithParams = tagsWithParams.get(1);
    assertEquals(i18nTagWithParams.getTagType(), TagType.VAR);
    assertEquals(i18nTagWithParams.getTag(), "i18n");
    assertEquals(i18nTagWithParams.getParams().size(), 1);
    Param param = i18nTagWithParams.getParams().get(0);
    StrParam strParam = (StrParam) param;
    assertEquals(strParam.apply(null), "value1");
  }

  @Test
  public void collectWithParamsNoParamsTest() throws IOException {
    List<TagWithParams> tagsWithParams = getTagsWithParameters("{{test}}");
    assertEquals(tagsWithParams.size(), 1);
    TagWithParams tagWithParams = tagsWithParams.get(0);
    assertEquals(tagWithParams.getTag(), "test");
    assertTrue(tagWithParams.getParams().isEmpty());
  }

  private List<TagWithParams> getTagsWithParameters(String input) throws IOException {
    Handlebars hb = newHandlebars();
    Template template = hb.compileInline(input);
    return new ArrayList<>(template.collectWithParameters(TagType.values()));
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
