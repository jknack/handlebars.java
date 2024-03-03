/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue459 extends v4Test {

  @Test
  public void stringLiteralInPartials() throws IOException {
    shouldCompileTo(
        "{{> \"loop\"}}", $("hash", $("foo", "bar"), "partials", $("loop", "{{foo}}")), "bar");

    shouldCompileTo(
        "{{> 'loop'}}", $("hash", $("foo", "bar"), "partials", $("loop", "{{foo}}")), "bar");

    shouldCompileTo(
        "{{> \"loop\" this}}", $("hash", $("foo", "bar"), "partials", $("loop", "{{foo}}")), "bar");

    shouldCompileTo(
        "{{> \"loop\" h=1}}",
        $("hash", $("foo", "bar"), "partials", $("loop", "{{foo}}{{h}}")),
        "bar1");
  }
}
