package com.github.jknack.handlebars.i251;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue251 extends AbstractTest {

  @Test
  public void parseTilde() throws Exception {
    // vars
    shouldCompileTo("{{~tilde}}", $, "");

    shouldCompileTo("{{tilde ~}}", $, "");

    shouldCompileTo("{{~tilde ~}}", $, "");

    // amp vars
    shouldCompileTo("{{~&tilde}}", $, "");
    shouldCompileTo("{{&tilde~}}", $, "");
    shouldCompileTo("{{~&tilde~}}", $, "");

    // triple vars
    shouldCompileTo("{{~{tilde}}}", $, "");
    shouldCompileTo("{{{tilde}~}}", $, "");
    shouldCompileTo("{{~{tilde}~}}", $, "");

    // block
    shouldCompileTo("{{~#tilde}}{{/tilde}}", $, "");
    shouldCompileTo("{{#tilde~}}{{/tilde}}", $, "");
    shouldCompileTo("{{#tilde}}{{~/tilde}}", $, "");
    shouldCompileTo("{{#tilde}}{{/tilde~}}", $, "");
    shouldCompileTo("{{~#tilde~}}{{~/tilde~}}", $, "");
  }

  @Test
  public void varTrimLeft() throws IOException {
    shouldCompileTo("    {{~this}}", "trim-left", "trim-left");

    shouldCompileTo("  x  {{~this}}", "trim-left", "  xtrim-left");

    shouldCompileTo("x  {{~this}}", "trim-left", "xtrim-left");

    shouldCompileTo("   x{{~this}}", "trim-left", "   xtrim-left");
  }

  @Test
  public void varTrimRight() throws IOException {
    shouldCompileTo("{{this ~}}   ", "trim-right", "trim-right");

    shouldCompileTo("{{this ~}}  x ", "trim-right", "trim-rightx ");

    shouldCompileTo("{{this ~}}   x", "trim-right", "trim-rightx");

    shouldCompileTo("{{this ~}}x   ", "trim-right", "trim-rightx   ");
  }

  @Test
  public void varTrim() throws IOException {
    shouldCompileTo("{{~this~}}", "trim", "trim");
    shouldCompileTo("    {{~this~}}  ", "trim", "trim");
    shouldCompileTo("  x  {{~this~}}  z ", "trim", "  xtrimz ");
  }

  @Test
  public void whiteSpaceControl() throws IOException {
    shouldCompileTo("{{#each nav ~}}\n" +
        "  <a href=\"{{url}}\">\n" +
        "    {{~#if test}}\n" +
        "      {{~title}}\n" +
        "    {{~^~}}\n" +
        "      Empty\n" +
        "    {{~/if~}}\n" +
        "  </a>\n" +
        "{{~/each}}",
        $("nav", new Object[]{
            $("url", "foo", "test", true, "title", "bar"),
            $("url", "bar")
        }),
        "<a href=\"foo\">bar</a><a href=\"bar\">Empty</a>");
  }
}
