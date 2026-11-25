/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/partials.js */
@DisplayName("partials")
public class PartialsTest extends AbstractTest {

  private boolean prettyPrint;

  private boolean preventIndent;

  private boolean explicitPartialContext;

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.setPrettyPrint(prettyPrint);
    handlebars.preventIndent(preventIndent).explicitPartialContext(explicitPartialContext);
  }

  private static Object dudes() {
    return $(
        "dudes",
        new Object[] {
          $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
        });
  }

  private static Object jeepers() {
    return $("name", "Jeepers", "anotherDude", "Creepers");
  }

  @Test
  @DisplayName("basic partials")
  public void basicpartials() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{#dudes}}{{> dude}}{{/dudes}}",
        dudes(),
        $("dude", "{{name}} ({{url}}) "),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ");
  }

  @Test
  @DisplayName("dynamic partials")
  public void dynamicpartials() throws IOException {
    Hash helpers = $("partial", (Helper<Object>) (context, options) -> "dude");
    shouldCompileTo(
        "Dudes: {{#dudes}}{{> (partial)}}{{/dudes}}",
        dudes(),
        helpers,
        $("dude", "{{name}} ({{url}}) "),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ",
        "");
  }

  @Test
  @DisplayName("failing dynamic partials")
  public void failingdynamicpartials() {
    Hash helpers = $("partial", (Helper<Object>) (context, options) -> "missing");
    assertThrows(
        HandlebarsException.class,
        () ->
            compile(
                    "Dudes: {{#dudes}}{{> (partial)}}{{/dudes}}",
                    helpers,
                    $("dude", "{{name}} ({{url}}) "))
                .apply(dudes()));
  }

  @Test
  @DisplayName("partials with context")
  public void partialswithcontext() throws IOException {
    Hash helpers = $("partial", (Helper<Object>) (context, options) -> "missing");
    shouldCompileTo(
        "Dudes: {{>dude dudes}}",
        dudes(),
        helpers,
        $("dude", "{{#this}}{{name}} ({{url}}) {{/this}}"),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ",
        "");
  }

  @Test
  @DisplayName("partials with no context")
  public void partialswithnocontext() throws IOException {
    explicitPartialContext = true;
    shouldCompileToWithPartials(
        "Dudes: {{#dudes}}{{>dude}}{{/dudes}}",
        dudes(),
        $("dude", "{{name}} ({{url}}) "),
        "Dudes:  ()  () ");

    shouldCompileToWithPartials(
        "Dudes: {{#dudes}}{{>dude name=\"foo\"}}{{/dudes}}",
        dudes(),
        $("dude", "{{name}} ({{url}}) "),
        "Dudes: foo () foo () ");
  }

  @Test
  @DisplayName("partials with string context")
  public void partialswithstringcontext() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{>dude \"dudes\"}}", $, $("dude", "{{.}}"), "Dudes: dudes");
  }

  @Test
  @DisplayName("partials with undefined context")
  public void partialswithundefinedcontext() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{>dude dudes}}", $, $("dude", "{{foo}} Empty"), "Dudes:  Empty");
  }

  @Test
  @DisplayName("partials with duplicate parameters")
  public void partialswithduplicateparameters() {
    assertThrows(
        HandlebarsException.class,
        () ->
            compile("Dudes: {{>dude dudes foo bar=baz}}", $(), $("dude", "{{foo}} Empty"))
                .apply($));
  }

  @Test
  @DisplayName("partials with parameters")
  public void partialswithparameters() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{#dudes}}{{> dude others=..}}{{/dudes}}",
        $(
            "foo",
            "bar",
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            }),
        $("dude", "{{others.foo}}{{name}} ({{url}}) "),
        "Dudes: barYehuda (http://yehuda) barAlan (http://alan) ");
  }

  @Test
  @DisplayName("partial in a partial")
  public void partialinpartial() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{#dudes}}{{>dude}}{{/dudes}}",
        dudes(),
        $("dude", "{{name}} {{> url}} ", "url", "<a href=\"{{url}}\">{{url}}</a>"),
        "Dudes: Yehuda <a href=\"http://yehuda\">http://yehuda</a> Alan <a"
            + " href=\"http://alan\">http://alan</a> ");
  }

  @Test
  @DisplayName("rendering undefined partial throws an exception")
  public void renderingundefinedpartialthrowsanexception() {
    assertThrows(HandlebarsException.class, () -> compile("{{> whatever}}").apply($));
  }

  @Test
  @DisplayName("a partial preceding a selector")
  public void apartialprecedingaselector() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{>dude}} {{anotherDude}}",
        jeepers(),
        $("dude", "{{name}}"),
        "Dudes: Jeepers Creepers");
  }

  @Test
  @DisplayName("Partials with slash paths")
  public void partialswithslashpaths() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{> shared/dude}}", jeepers(), $("shared/dude", "{{name}}"), "Dudes: Jeepers");
  }

  @Test
  @DisplayName("Partials with slash and point paths")
  public void partialswithslashandpointpaths() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{> shared/dude.thing}}",
        jeepers(),
        $("shared/dude.thing", "{{name}}"),
        "Dudes: Jeepers");
  }

  @Test
  @DisplayName("Partials with integer path")
  public void partialswithintegerpath() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{> 404}}", jeepers(), $("404", "{{name}}"), "Dudes: Jeepers");
  }

  @Test
  @DisplayName("Partials with complex path")
  public void partialswithcomplexpath() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{> 404/asdf?.bar}}", jeepers(), $("404/asdf?.bar", "{{name}}"), "Dudes: Jeepers");
  }

  @Test
  @DisplayName("Partials with escaped")
  public void Partialswithescaped() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{> [+404/asdf?.bar]}}",
        jeepers(),
        $("+404/asdf?.bar", "{{name}}"),
        "Dudes: Jeepers");
  }

  @Test
  @DisplayName("Partials with string")
  public void Partialswithstring() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{> '+404/asdf?.bar'}}",
        jeepers(),
        $("+404/asdf?.bar", "{{name}}"),
        "Dudes: Jeepers");

    shouldCompileToWithPartials(
        "Dudes: {{> \"+404/asdf?.bar\"}}",
        jeepers(),
        $("+404/asdf?.bar", "{{name}}"),
        "Dudes: Jeepers");
  }

  @Test
  @DisplayName("should handle empty partial")
  public void shouldhandleemptypartial() throws IOException {
    shouldCompileToWithPartials(
        "Dudes: {{#dudes}}{{> dude}}{{/dudes}}", dudes(), $("dude", ""), "Dudes: ");
  }

  @Test
  @DisplayName("should render partial block as default")
  public void shouldrenderpartialblockasdefault() throws IOException {
    shouldCompileTo("{{#> dude}}success{{/dude}}", $, "success");
  }

  @Test
  @DisplayName("should execute default block with proper context")
  public void shouldexecutedefaultblockwithpropercontext() throws IOException {
    shouldCompileTo(
        "{{#> dude context}}{{value}}{{/dude}}", $("context", $("value", "success")), "success");
  }

  @Test
  @DisplayName("should propagate block parameters to default block")
  public void shouldpropagateblockparameterstodefaultblock() throws IOException {
    shouldCompileTo(
        "{{#with context as |me|}}{{#> dude}}{{me.value}}{{/dude}}{{/with}}",
        $("context", $("value", "success")),
        "success");
  }

  @Test
  @DisplayName("should not use partial block if partial exists")
  public void shouldnotusepartialblockifpartialexists() throws IOException {
    shouldCompileToWithPartials("{{#> dude}}fail{{/dude}}", $, $("dude", "success"), "success");
  }

  @Test
  @DisplayName("should render block from partial")
  public void shouldrenderblockfrompartial() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}success{{/dude}}", $, $("dude", "{{> @partial-block }}"), "success");
  }

  @Test
  @DisplayName("should be able to render the partial-block twice")
  public void shouldbeabletorenderthepartialblocktwice() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}success{{/dude}}",
        $,
        $("dude", "{{> @partial-block }} {{> @partial-block }}"),
        "success success");
  }

  @Test
  @DisplayName("should render block from partial with context")
  public void shouldrenderblockfrompartialwithcontext() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}{{value}}{{/dude}}",
        $("context", $("value", "success")),
        $("dude", "{{#with context}}{{> @partial-block }}{{/with}}"),
        "success");
  }

  @Test
  @DisplayName("should be able to access the @data frame from a partial-block")
  public void shouldbeabletoaccessthedataframefromapartialblock() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}in-block: {{@root/value}}{{/dude}}",
        $("value", "success"),
        $("dude", "<code>before-block: {{@root/value}} {{>   @partial-block }}</code>"),
        "<code>before-block: success in-block: success</code>");
  }

  @Test
  @DisplayName("should allow the #each-helper to be used along with partial-blocks")
  public void shouldallowtheeachhelpertobeusedalongwithpartialblocks() throws IOException {
    shouldCompileToWithPartials(
        "<template>{{#> list value}}value = {{.}}{{/list}}</template>",
        $("value", new Object[] {"a", "b", "c"}),
        $("list", "<list>{{#each .}}<item>{{> @partial-block}}</item>{{/each}}</list>"),
        "<template><list><item>value = a</item><item>value = b</item><item>value ="
            + " c</item></list></template>");
  }

  @Test
  @DisplayName("should render block from partial with context (twice)")
  public void shouldrenderblockfrompartialwithcontexttwice() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}{{value}}{{/dude}}",
        $("context", $("value", "success")),
        $("dude", "{{#with context}}{{> @partial-block }} {{> @partial-block }}{{/with}}"),
        "success success");
  }

  @Test
  @DisplayName("should render block from partial with block params")
  public void shouldrenderblockfrompartialwithblockparams() throws IOException {
    shouldCompileToWithPartials(
        "{{#with context as |me|}}{{#> dude}}{{me.value}}{{/dude}}{{/with}}",
        $("context", $("value", "success")),
        $("dude", "{{> @partial-block }}"),
        "success");
  }

  @Test
  @DisplayName("should render nested partial blocks")
  public void shouldrendernestedpartialblocks() throws IOException {
    shouldCompileToWithPartials(
        "<template>{{#> outer}}{{value}}{{/outer}}</template>",
        $("value", "success"),
        $(
            "outer",
            "<outer>{{#> nested}}<outer-block>{{>"
                + " @partial-block}}</outer-block>{{/nested}}</outer>",
            "nested",
            "<nested>{{> @partial-block}}</nested>"),
        "<template><outer><nested><outer-block>success</outer-block></nested></outer></template>");
  }

  @Test
  @DisplayName("should render nested partial blocks at different nesting levels")
  public void shouldrendernestedpartialblocksatdifferentnestinglevels() throws IOException {
    shouldCompileToWithPartials(
        "<template>{{#> outer}}{{value}}{{/outer}}</template>",
        $("value", "success"),
        $(
            "outer",
            "<outer>{{#> nested}}<outer-block>{{>"
                + " @partial-block}}</outer-block>{{/nested}}</outer>",
            "nested",
            "<nested>{{> @partial-block}}</nested>"),
        "<template><outer><nested><outer-block>success</outer-block></nested></outer></template>");
  }

  @Test
  @DisplayName("should render nested partial blocks at different nesting levels (twice)")
  public void shouldrendernestedpartialblocksatdifferentnestinglevelstwice() throws IOException {
    shouldCompileToWithPartials(
        "<template>{{#> outer}}{{value}}{{/outer}}</template>",
        $("value", "success"),
        $(
            "outer",
            "<outer>{{#> nested}}<outer-block>{{> @partial-block}} {{>"
                + " @partial-block}}</outer-block>{{/nested}}{{> @partial-block}}+{{>"
                + " @partial-block}}</outer>",
            "nested",
            "<nested>{{> @partial-block}}</nested>"),
        "<template><outer><nested><outer-block>success"
            + " success</outer-block></nested>success+success</outer></template>");
  }

  @Test
  @DisplayName("should render nested partial blocks (twice at each level)")
  public void shouldrendernestedpartialblockstwiceateachlevel() throws IOException {
    shouldCompileToWithPartials(
        "<template>{{#> outer}}{{value}}{{/outer}}</template>",
        $("value", "success"),
        $(
            "outer",
            "<outer>{{#> nested}}<outer-block>{{> @partial-block}} {{>"
                + " @partial-block}}</outer-block>{{/nested}}</outer>",
            "nested",
            "<nested>{{> @partial-block}}{{> @partial-block}}</nested>"),
        "<template><outer><nested><outer-block>success"
            + " success</outer-block><outer-block>success"
            + " success</outer-block></nested></outer></template>");
  }

  @Test
  @DisplayName("should define inline partials for template")
  public void shoulddefineinlinepartialsfortemplate() throws IOException {
    shouldCompileTo("{{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}", $, "success");
  }

  @Test
  @DisplayName("should overwrite multiple partials in the same template")
  public void shouldoverwritemultiplepartialsinthesametemplate() throws IOException {
    shouldCompileTo(
        "{{#*inline \"myPartial\"}}fail{{/inline}}{{#*inline"
            + " \"myPartial\"}}success{{/inline}}{{> myPartial}}",
        $,
        "success");
  }

  @Test
  @DisplayName("should define inline partials for block")
  public void shoulddefineinlinepartialsforblock() throws IOException {
    shouldCompileTo(
        "{{#with .}}{{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}{{/with}}",
        true,
        "success");

    assertThrows(
        HandlebarsException.class,
        () ->
            compile(
                    "{{#with .}}{{#*inline \"myPartial\"}}success{{/inline}}{{/with}}{{>"
                        + " myPartial}}")
                .apply(true));
  }

  @Test
  @DisplayName("should override global partials")
  public void shouldoverrideglobalpartials() throws IOException {
    shouldCompileToWithPartials(
        "{{#*inline \"myPartial\"}}success{{/inline}}{{> myPartial}}",
        $,
        $("myPartial", "fail"),
        "success");
  }

  @Test
  @DisplayName("should override template partials")
  public void shouldoverridetemplatepartials() throws IOException {
    shouldCompileTo(
        "{{#*inline \"myPartial\"}}fail{{/inline}}{{#with .}}{{#*inline"
            + " \"myPartial\"}}success{{/inline}}{{> myPartial}}{{/with}}",
        true,
        "success");
  }

  @Test
  @DisplayName("should override partials down the entire stack")
  public void shouldoverridepartialsdowntheentirestack() throws IOException {
    shouldCompileTo(
        "{{#with .}}{{#*inline \"myPartial\"}}success{{/inline}}{{#with .}}{{#with .}}{{>"
            + " myPartial}}{{/with}}{{/with}}{{/with}}",
        true,
        "success");
  }

  @Test
  @DisplayName("should define inline partials for partial call")
  public void shoulddefineinlinepartialsforpartialcall() throws IOException {
    shouldCompileToWithPartials(
        "{{#*inline \"myPartial\"}}success{{/inline}}{{> dude}}",
        $,
        $("dude", "{{> myPartial}}"),
        "success");
  }

  @Test
  @DisplayName("should define inline partials in partial block call")
  public void shoulddefineinlinepartialsinpartialblockcall() throws IOException {
    shouldCompileToWithPartials(
        "{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        $,
        $("dude", "{{> myPartial}}"),
        "success");
  }

  @Test
  @DisplayName("should render nested inline partials")
  public void shouldrendernestedinlinepartials() throws IOException {
    shouldCompileTo(
        "{{#*inline"
            + " \"outer\"}}{{#>inner}}<outer-block>{{>@partial-block}}</outer-block>{{/inner}}{{/inline}}{{#*inline"
            + " \"inner\"}}<inner>{{>@partial-block}}</inner>{{/inline}}"
            + "{{#>outer}}{{value}}{{/outer}}",
        $("value", "success"),
        "<inner><outer-block>success</outer-block></inner>");
  }

  @Test
  @DisplayName(
      "should render nested inline partials with partial-blocks on different nesting levels")
  public void shouldrendernestedinlinepartialswithpartialblocksondifferentnestinglevels()
      throws IOException {
    shouldCompileTo(
        "{{#*inline"
            + " \"outer\"}}{{#>inner}}<outer-block>{{>@partial-block}}</outer-block>{{/inner}}{{>@partial-block}}{{/inline}}{{#*inline"
            + " \"inner\"}}<inner>{{>@partial-block}}</inner>{{/inline}}"
            + "{{#>outer}}{{value}}{{/outer}}",
        $("value", "success"),
        "<inner><outer-block>success</outer-block></inner>success");
  }

  @Test
  @DisplayName("should render nested inline partials (twice at each level)")
  public void shouldrendernestedinlinepartialstwiceateachlevel() throws IOException {
    shouldCompileTo(
        "{{#*inline \"outer\"}}{{#>inner}}<outer-block>{{>@partial-block}}"
            + " {{>@partial-block}}</outer-block>{{/inner}}{{/inline}}{{#*inline"
            + " \"inner\"}}<inner>{{>@partial-block}}{{>@partial-block}}</inner>{{/inline}}"
            + "{{#>outer}}{{value}}{{/outer}}",
        $("value", "success"),
        "<inner><outer-block>success success</outer-block><outer-block>success"
            + " success</outer-block></inner>");
  }

  @Test
  @DisplayName("indented partials")
  public void indentedpartials() throws IOException {
    prettyPrint = true;
    shouldCompileToWithPartials(
        "Dudes:\n{{#dudes}}\n  {{>dude}}\n{{/dudes}}",
        dudes(),
        $("dude", "{{name}}\n"),
        "Dudes:\n  Yehuda\n  Alan\n");
  }

  @Test
  @DisplayName("nested indented partials")
  public void nestedindentedpartials() throws IOException {
    prettyPrint = true;
    shouldCompileToWithPartials(
        "Dudes:\n{{#dudes}}\n  {{>dude}}\n{{/dudes}}",
        dudes(),
        $("dude", "{{name}}\n {{> url}}", "url", "{{url}}!\n"),
        "Dudes:\n  Yehuda\n   http://yehuda!\n  Alan\n   http://alan!\n");
  }

  @Test
  @DisplayName("prevent nested indented partials")
  public void preventnestedindentedpartials() throws IOException {
    prettyPrint = true;
    preventIndent = true;
    shouldCompileToWithPartials(
        "Dudes:\n{{#dudes}}\n  {{>dude}}\n{{/dudes}}",
        dudes(),
        $("dude", "{{name}}\n {{> url}}", "url", "{{url}}!\n"),
        "Dudes:\n  Yehuda\n http://yehuda!\n  Alan\n http://alan!\n");
  }

  @Test
  @DisplayName("validate *inline usage")
  public void validateInlineUsage() {
    HandlebarsException noArgs =
        assertThrows(
            HandlebarsException.class, () -> compile("{{#*inline}}success{{/inline}}").apply($));
    assertTrue(noArgs.getMessage().contains("inline"), noArgs.getMessage());

    HandlebarsException notLiteral =
        assertThrows(
            HandlebarsException.class,
            () -> compile("{{#*inline reference}}success{{/inline}}").apply($));
    assertTrue(notLiteral.getMessage().contains("inline"), notLiteral.getMessage());
  }

  @Test
  @DisplayName("*inline partial overrides real partial")
  public void inlinePartialOverridesRealPartial() throws IOException {
    shouldCompileToWithPartials(
        "{{#*inline \"myPartial\"}}before{{/inline}}{{> myPartial}}",
        $,
        $("myPartial", "real"),
        "before");

    shouldCompileToWithPartials(
        "{{> myPartial}}{{#*inline \"myPartial\"}}after{{/inline}}",
        $,
        $("myPartial", "real"),
        "after");
  }
}
