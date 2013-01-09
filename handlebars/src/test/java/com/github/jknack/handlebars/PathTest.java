package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

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
