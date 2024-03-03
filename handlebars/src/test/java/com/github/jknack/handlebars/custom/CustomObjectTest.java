/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.custom;

import java.io.IOException;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import mustache.specs.Spec;
import mustache.specs.SpecTest;

public class CustomObjectTest extends SpecTest {

  public CustomObjectTest(final Spec spec) {
    super(spec);
  }

  @Parameters
  public static Collection<Object[]> data() throws IOException {
    return data(CustomObjectTest.class, "customObjects.yml");
  }
}
