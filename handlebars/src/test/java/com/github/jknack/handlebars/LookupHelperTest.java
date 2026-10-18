/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.helper.LookupHelper;

class LookupHelperTest extends AbstractTest {

  @Test
  void nullKeyFallsBackToContext() throws IOException {
    Hash context = new Hash().$("name", "fallback");
    assertSame(context, lookup(context, (Object) null));
  }

  @Test
  void nullKeyFallsBackToNonMapContext() throws IOException {
    Object context = new Object();
    assertSame(context, lookup(context, (Object) null));
  }

  @Test
  void missingNestedDynamicKeyRendersFallback() throws IOException {
    shouldCompileTo(
        "{{#with (lookup object person.id)}}{{name}}{{/with}}",
        new Hash().$("object", new Hash().$("name", "fallback")).$("person", new Hash()),
        "fallback");
  }

  @Test
  void explicitNullDynamicKeyRendersFallback() throws IOException {
    shouldCompileTo(
        "{{#with (lookup object key)}}{{name}}{{/with}}",
        new Hash().$("object", new Hash().$("name", "fallback")).$("key", null),
        "fallback");
  }

  @Test
  void nullParentKeyFallsBackInsideEach() throws IOException {
    shouldCompileTo(
        "{{#each objects}}{{#with (lookup this ../key)}}{{name}};{{/with}}{{/each}}",
        new Hash()
            .$("objects", Arrays.asList(new Hash().$("name", "A"), new Hash().$("name", "B")))
            .$("key", null),
        "A;B;");
  }

  @Test
  void nullKeyDoesNotLookUpTheStringNull() throws IOException {
    Hash context = new Hash().$("name", "fallback").$("null", "literal");
    assertSame(context, lookup(context, (Object) null));
  }

  @Test
  void stringNullRemainsAPropertyName() throws IOException {
    assertEquals("literal", lookup(new Hash().$("null", "literal"), "null"));
  }

  @Test
  void nullContextRemainsNull() throws IOException {
    assertNull(lookup(null, "name"));
  }

  @Test
  void nullContextAndKeyRemainNull() throws IOException {
    assertNull(lookup(null, (Object) null));
  }

  @Test
  void missingParameterFallsBackToContext() throws IOException {
    Hash context = new Hash().$("name", "fallback");
    assertSame(context, lookup(context));
  }

  @Test
  void missingPropertyFallsBackToContext() throws IOException {
    Hash context = new Hash().$("name", "fallback");
    assertSame(context, lookup(context, "absent"));
  }

  @Test
  void nullPropertyFallsBackToContext() throws IOException {
    Hash context = new Hash().$("value", null);
    assertSame(context, lookup(context, "value"));
  }

  @Test
  void falsePropertyIsReturned() throws IOException {
    assertEquals(false, lookup(new Hash().$("value", false), "value"));
  }

  @Test
  void zeroPropertyIsReturned() throws IOException {
    assertEquals(0, lookup(new Hash().$("value", 0), "value"));
  }

  @Test
  void emptyPropertyIsReturned() throws IOException {
    assertEquals("", lookup(new Hash().$("value", ""), "value"));
  }

  @Test
  void nestedPropertyIsReturned() throws IOException {
    assertEquals(
        "found", lookup(new Hash().$("person", new Hash().$("name", "found")), "person.name"));
  }

  private Object lookup(final Object context, final Object... params) throws IOException {
    Handlebars handlebars = new Handlebars();
    Options options =
        new Options.Builder(
                handlebars,
                LookupHelper.NAME,
                TagType.VAR,
                Context.newContext(context),
                handlebars.compileInline(""))
            .setParams(params)
            .build();
    return LookupHelper.INSTANCE.apply(context, options);
  }
}
