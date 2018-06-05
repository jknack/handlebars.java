package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class PartialBlockTest extends AbstractTest {

  @Test
  public void text() throws IOException {
    assertEquals("{{#>dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        compile("{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}").text());

    assertEquals("{{#>dude}}success{{/dude}}",
        compile("{{#> dude}}success{{/dude}}").text());
  }

  @Test
  public void shouldDefineInlinePartialsInPartialBlockCall() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        $, $("dude", "{{> myPartial }}"), "success");
  }

  @Test
  public void shouldRenderPartialBlockAsDefault() throws IOException {
    shouldCompileToWithPartials("{{#> dude}}success{{/dude}}", $, $(), "success");
  }

  @Test
  public void shouldExecuteDefaultBlockWithProperContext() throws IOException {
    shouldCompileToWithPartials("{{#> dude context}}{{value}}{{/dude}}",
        $("context", $("value", "success")), $(), "success");
  }

  @Test
  public void shouldPropagateBlockParametersToDefaultBlock() throws IOException {
    shouldCompileToWithPartials(
        "{{#with context as |me|}}{{#> dude}}{{me.value}}{{/dude}}{{/with}}",
        $("context", $("value", "success")), $(), "success");
  }

  @Test
  public void shouldNotUsePartialBlockIfPartialExists() throws IOException {
    shouldCompileToWithPartials("{{#> dude}}fail{{/dude}}",
        $, $("dude", "success"), "success");
  }

  @Test
  public void shouldRenderBlockFromPartial() throws IOException {
    shouldCompileToWithPartials("{{#> dude}}success{{/dude}}",
        $, $("dude", "{{> @partial-block }}"), "success");
  }

  @Test
  public void shouldRenderBlockFromPartialWithContext() throws IOException {
    shouldCompileToWithPartials("{{#> dude}}{{value}}{{/dude}}",
        $("context", $("value", "success")),
        $("dude", "{{#with context}}{{> @partial-block }}{{/with}}"), "success");
  }

  @Test
  public void shouldRenderBlockFromPartialWithPathedContext() throws IOException {
    shouldCompileToWithPartials("{{#> dude}}{{../context/value}}{{/dude}}",
        $("context", $("value", "success")),
        $("dude", "{{#with context}}{{> @partial-block }}{{/with}}"), "success");
  }

  @Test
  public void shouldRenderBlockFromPartialWithBlockParams() throws IOException {
    shouldCompileToWithPartials(
        "{{#with context as |me|}}{{#> dude}}{{me.value}}{{/dude}}{{/with}}",
        $("context", $("value", "success")),
        $("dude", "{{#with context}}{{> @partial-block }}{{/with}}"), "success");
  }

  @Test
  public void eachInlinePartialIsAvailableToTheCurrentBlockAndAllChildren() throws IOException {
    shouldCompileToWithPartials(
        "{{#> layout}}\n" +
        "  {{#*inline \"nav\"}}\n" +
        "    My Nav\n" +
        "  {{/inline}}\n" +
        "  {{#*inline \"content\"}}\n" +
        "    My Content\n" +
        "  {{/inline}}\n" +
        "{{/layout}}",
        $,
        $("layout", "<div class=\"nav\">\n" +
            "  {{> nav}}\n" +
            "</div>\n" +
            "<div class=\"content\">\n" +
            "  {{> content}}\n" +
            "</div>"), "<div class=\"nav\">\n" +
                "  \n" +
                "    My Nav\n" +
                "  \n" +
                "</div>\n" +
                "<div class=\"content\">\n" +
                "  \n" +
                "    My Content\n" +
                "  \n" +
                "</div>");
  }

}
