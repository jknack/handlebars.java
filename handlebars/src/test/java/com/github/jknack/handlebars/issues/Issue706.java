/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue706 extends AbstractTest {

  record SEOValue(String field1) {}

  record TestContext(SEOValue[] SEOValueArray) {}

  @Test
  public void expressionCanStartWithNumber() throws IOException {
    shouldCompileTo(
        "{{#each SEOValueArray}}{{field1}}{{/each}}",
        new TestContext(new SEOValue[] {new SEOValue("foo")}),
        "foo");
  }
}
