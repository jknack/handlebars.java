/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue473 extends AbstractTest {

  @Test
  public void testWith() throws IOException {
    Hash context = $("needsPano", Boolean.TRUE, "group", $("title", "test"));

    shouldCompileTo("{{#with group}}{{needsPano}}{{/with}}", context, "true");
  }

  @Test
  public void testBlock() throws IOException {
    Hash context = $("needsPano", Boolean.TRUE, "group", $("title", "test"));

    shouldCompileTo("{{#group}}{{needsPano}}{{/group}}", context, "true");
  }
}
