package com.github.jknack.handlebars;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PartialBlockTest extends AbstractTest {

  @Test
  public void text() throws IOException {
    assertEquals("{{#>dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        compile("{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}").text());

    assertEquals("{{#>dude}}success{{/dude}}",
        compile("{{#> dude}}success{{/dude}}").text());
  }

  @Test
  public void shouldDefineInlinePartialsInPartialBlockCallByDefault() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        $, $("dude", "{{#> myPartial }}{{/myPartial}}"), "success");
  }

  @Test
  public void shouldNotDefineInlinePartialsInPartialBlockCallWithoutPreEvaluation() throws IOException {
    shouldCompileToWithPartialsWithoutPreEvaluation(
            "{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
            $, $("dude", "{{#> myPartial }}{{/myPartial}}"), "");
  }
  @Test
  public void shouldDefineInlinePartialsInPartialBlockCall() throws IOException {
    shouldCompileToWithPartials(
            "{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
            $, $("dude", "{{> @partial-block}}{{#> myPartial }}{{/myPartial}}"), "success");
  }

  @Test
  public void shouldOverrideBlockParams() throws IOException {
    shouldCompileToWithPartials("{{#> dude x=23}}{{#> dude x=12}}{{/dude}}{{/dude}}",
            $, $("dude", "<div {{#if x}}x={{x}}{{/if}}>{{> @partial-block}}</div>"), "<div x=23><div x=12></div></div>");
  }

  @Test
  public void shouldOverrideBlockParamsWithoutPreEvaluation() throws IOException {
    shouldCompileToWithPartialsWithoutPreEvaluation("{{#> dude x=23}}{{#> dude x=12}}{{/dude}}{{/dude}}",
            $, $("dude", "<div {{#if x}}x={{x}}{{/if}}>{{> @partial-block}}</div>"), "<div x=23><div x=12></div></div>");
  }

  @Test
  public void shouldOverrideBlockParamsWithFalse() throws IOException {
    shouldCompileToWithPartials("{{#> dude x=23}}{{#> dude x=false}}{{/dude}}{{/dude}}",
            $, $("dude", "<div {{#if x}}x={{x}}{{/if}}>{{> @partial-block}}</div>"), "<div x=23><div ></div></div>");
  }

  @Test
  public void shouldDefineInlinePartialsInPartialCall() throws IOException {
    shouldCompileToWithPartials(
            "{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
            $, $("dude", "{{> @partial-block}}{{> myPartial }}"), "success");
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

  @Ignore
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
