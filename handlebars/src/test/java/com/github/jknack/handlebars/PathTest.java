/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class PathTest extends AbstractTest {

  @Test
  public void paths() throws IOException {
    Hash context = $("title", "root", "foo", $("title", "foo", "bar", $("title", "bar")));

    shouldCompileTo("{{#foo}}{{#bar}}{{title}}{{/bar}}{{/foo}}", context, "bar");

    shouldCompileTo("{{#foo}}{{#bar}}{{../title}}{{/bar}}{{/foo}}", context, "foo");

    shouldCompileTo("{{#foo}}{{#bar}}{{../../title}}{{/bar}}{{/foo}}", context, "root");

    shouldCompileTo("{{#foo}}{{#bar}}{{../../../title}}{{/bar}}{{/foo}}", context, "");
  }
}
