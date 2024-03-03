/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class LazyPartialBlockEvaluationTest extends AbstractTest {
  @Override
  protected void configure(Handlebars handlebars) {
    handlebars.setPreEvaluatePartialBlocks(false);
  }

  @Test
  public void shouldSupportMultipleLevelsOfNestedPartialBlocks() throws IOException {
    String myMoreNestedPartial = "I{{> @partial-block}}I";
    String myNestedPartial =
        "A{{#> myMoreNestedPartial}}{{> @partial-block}}{{/myMoreNestedPartial}}B";
    String myPartial = "{{#> myNestedPartial}}{{> @partial-block}}{{/myNestedPartial}}";
    Template t =
        compile(
            "C{{#> myPartial}}hello{{/myPartial}}D",
            new Hash(),
            $(
                "myPartial",
                myPartial,
                "myNestedPartial",
                myNestedPartial,
                "myMoreNestedPartial",
                myMoreNestedPartial));
    String result = t.apply(null);
    assertEquals("'CAIhelloIBD' should === '" + result + "': ", "CAIhelloIBD", result);
  }

  @Test(expected = HandlebarsException.class)
  public void shouldNotDefineInlinePartialsInPartialBlockCall() throws IOException {
    // myPartial should not be defined and thus throw a handlebars exception
    shouldCompileToWithPartials(
        "{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        $,
        $("dude", "{{> myPartial }}"),
        "");
  }
}
