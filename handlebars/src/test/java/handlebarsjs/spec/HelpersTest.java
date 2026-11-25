/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/helpers.js */
@DisplayName("helpers")
public class HelpersTest extends AbstractTest {

  private static Map<?, ?> model(final Options options) {
    return (Map<?, ?>) options.context.model();
  }

  @Test
  @DisplayName("helper with complex lookup")
  public void helperWithComplexLookup() throws IOException {
    Hash helpers =
        $(
            "link",
            (Helper<Object>)
                (context, options) -> {
                  Map<?, ?> current = (Map<?, ?>) options.context.model();
                  return "<a href=\""
                      + context
                      + "/"
                      + current.get("url")
                      + "\">"
                      + current.get("text")
                      + "</a>";
                });
    shouldCompileTo(
        "{{#goodbyes}}{{{link ../prefix}}}{{/goodbyes}}",
        $("prefix", "/root", "goodbyes", new Object[] {$("text", "Goodbye", "url", "goodbye")}),
        helpers,
        "<a href=\"/root/goodbye\">Goodbye</a>");
  }

  @Test
  @DisplayName("helper for raw block gets raw content")
  public void helperForRawBlockGetsRawContent() throws IOException {
    shouldCompileTo(
        "{{{{raw}}}} {{test}} {{{{/raw}}}}",
        $("test", "hello"),
        $("raw", (Helper<Object>) (context, options) -> options.fn()),
        " {{test}} ");
  }

  @Test
  @DisplayName("helper for raw block gets parameters")
  public void helperForRawBlockGetsParameters() throws IOException {
    shouldCompileTo(
        "{{{{raw 1 2 3}}}} {{test}} {{{{/raw}}}}",
        $("test", "hello"),
        $(
            "raw",
            (Helper<Object>)
                (context, options) ->
                    options.fn().toString() + context + options.param(0) + options.param(1)),
        " {{test}} 123");
  }

  @Test
  @DisplayName("helper for nested raw block gets raw content")
  public void helperForNestedRawBlockGetsRawContent() throws IOException {
    shouldCompileTo(
        "{{{{identity}}}} {{{{b}}}} {{{{/b}}}} {{{{/identity}}}}",
        $,
        $("identity", (Helper<Object>) (context, options) -> options.fn()),
        " {{{{b}}}} {{{{/b}}}} ");
  }

  @Test
  @DisplayName("helper for nested raw block works with empty content")
  public void helperfornestedrawblockworkswithemptycontent() throws IOException {
    shouldCompileTo(
        "{{{{identity}}}}{{{{/identity}}}}",
        $,
        $("identity", (Helper<Object>) (context, options) -> options.fn()),
        "");
  }

  @Test
  @DisplayName("helper for nested raw block closes after first matching close")
  public void helperfornestedrawblockclosesafterfirstmatchingclose() throws IOException {
    shouldCompileTo(
        "{{{{identity}}}}abc{{{{/identity}}}} {{{{identity}}}}abc{{{{/identity}}}}",
        $,
        $("identity", (Helper<Object>) (context, options) -> options.fn()),
        "abc abc");
  }

  @Test
  @DisplayName("helper for nested raw block throw exception when with missing closing braces")
  public void helperfornestedrawblockthrowexceptionwhenwithmissingclosingbraces() {
    assertThrows(HandlebarsException.class, () -> compile("{{{{a}}}} {{{{/a").apply($));
  }

  @Test
  @DisplayName("helper block with identical context")
  public void helperBlockWithIdenticalContext() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{name}}{{/goodbyes}}",
        $("name", "Alan"),
        $(
            "goodbyes",
            (Helper<Object>)
                (context, options) -> {
                  StringBuilder out = new StringBuilder();
                  for (String bye : new String[] {"Goodbye", "goodbye", "GOODBYE"}) {
                    out.append(bye).append(" ").append(options.fn()).append("! ");
                  }
                  return out.toString();
                }),
        "Goodbye Alan! goodbye Alan! GOODBYE Alan! ");
  }

  @Test
  @DisplayName("helper block with complex lookup expression")
  public void helperBlockWithComplexLookupExpression() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{../name}}{{/goodbyes}}",
        $("name", "Alan"),
        $(
            "goodbyes",
            (Helper<Object>)
                (context, options) -> {
                  StringBuilder out = new StringBuilder();
                  for (String bye : new String[] {"Goodbye", "goodbye", "GOODBYE"}) {
                    out.append(bye).append(" ").append(options.fn(new Hash())).append("! ");
                  }
                  return out.toString();
                }),
        "Goodbye Alan! goodbye Alan! GOODBYE Alan! ");
  }

  @Test
  @DisplayName("helper with complex lookup and nested template")
  public void helperWithComplexLookupAndNestedTemplate() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{#link ../prefix}}{{text}}{{/link}}{{/goodbyes}}",
        $("prefix", "/root", "goodbyes", new Object[] {$("text", "Goodbye", "url", "goodbye")}),
        $(
            "link",
            (Helper<Object>)
                (context, options) -> {
                  Map<?, ?> current = model(options);
                  return "<a href=\""
                      + context
                      + "/"
                      + current.get("url")
                      + "\">"
                      + options.fn()
                      + "</a>";
                }),
        "<a href=\"/root/goodbye\">Goodbye</a>");
  }

  @Test
  @DisplayName("helper with complex lookup and nested template in VM+Compiler")
  public void helperwithcomplexlookupandnestedtemplateinVMCompiler() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{#link ../prefix}}{{text}}{{/link}}{{/goodbyes}}",
        $("prefix", "/root", "goodbyes", new Object[] {$("text", "Goodbye", "url", "goodbye")}),
        $(
            "link",
            (Helper<Object>)
                (context, options) -> {
                  Map<?, ?> current = model(options);
                  return "<a href=\""
                      + context
                      + "/"
                      + current.get("url")
                      + "\">"
                      + options.fn()
                      + "</a>";
                }),
        "<a href=\"/root/goodbye\">Goodbye</a>");
  }

  @Test
  @DisplayName("helper returning undefined value")
  public void helperReturningUndefinedValue() throws IOException {
    Hash helpers = $("nothere", (Helper<Object>) (context, options) -> null);
    shouldCompileTo(" {{nothere}}", $, helpers, " ");
    // no standalone stripping without prettyPrint
    shouldCompileTo(" {{#nothere}}{{/nothere}}", $, helpers, " ");
  }

  @Test
  @DisplayName("block helper executed")
  public void blockHelper() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{text}}! {{/goodbyes}}cruel {{world}}!",
        $("world", "world"),
        $("goodbyes", (Helper<Object>) (context, options) -> options.fn($("text", "GOODBYE"))),
        "GOODBYE! cruel world!");
  }

  @Test
  @DisplayName("block helper staying in the same context")
  public void blockHelperStayingInTheSameContext() throws IOException {
    shouldCompileTo(
        "{{#form}}<p>{{name}}</p>{{/form}}",
        $("name", "Yehuda"),
        $("form", (Helper<Object>) (context, options) -> "<form>" + options.fn() + "</form>"),
        "<form><p>Yehuda</p></form>");
  }

  public record View00(String name, int id) {
    public String link() {
      return "<a href=\"/people/" + id + "\">" + name + "</a>";
    }
  }

  public record View01(List<View00> people) {}

  public record Model00(String name, int id) {}

  public record Model01(List<Model00> people) {}

  @Test
  @DisplayName("block helper should have context in this")
  public void blockhelpershouldhavecontextinthis() throws IOException {
    String template = "<ul>{{#people}}<li>{{#link}}{{name}}{{/link}}</li>{{/people}}</ul>";
    String expected =
        "<ul><li><a href=\"/people/1\">Alan</a></li><li><a"
            + " href=\"/people/2\">Yehuda</a></li></ul>";
    Hash helpers =
        $(
            "link",
            (Helper<Object>)
                (context, options) ->
                    "<a href=\"/people/"
                        + model(options).get("id")
                        + "\">"
                        + options.fn()
                        + "</a>");
    shouldCompileTo(
        template,
        $("people", new Object[] {$("name", "Alan", "id", 1), $("name", "Yehuda", "id", 2)}),
        helpers,
        expected);
    shouldCompileTo(
        template,
        $("people", List.of($("name", "Alan", "id", 1), $("name", "Yehuda", "id", 2))),
        helpers,
        expected);
    shouldCompileTo(
        "<ul>{{#people}}<li>{{{link}}}</li>{{/people}}</ul>",
        new View01(List.of(new View00("Alan", 1), new View00("Yehuda", 2))),
        expected);
    shouldCompileTo(
        "<ul>{{#people}}<li>{{#link}}{{name}}{{/link}}</li>{{/people}}</ul>",
        new Model01(List.of(new Model00("Alan", 1), new Model00("Yehuda", 2))),
        $(
            "link",
            (Helper<Object>)
                (context, options) -> {
                  Model00 person = (Model00) context;
                  return "<a href=\"/people/" + person.id() + "\">" + options.fn(person) + "</a>";
                }),
        expected);
  }

  @Test
  @DisplayName("block helper for undefined value")
  public void blockhelperforundefinedvalue() throws IOException {
    shouldCompileTo("{{#missing}}shouldn't render{{/missing}}", $, "");
  }

  @Test
  @DisplayName("block helper passing a new context")
  public void blockHelperPassingExplicitContext() throws IOException {
    shouldCompileTo(
        "{{#form yehuda}}<p>{{name}}</p>{{/form}}",
        $("yehuda", $("name", "Yehuda")),
        $(
            "form",
            (Helper<Object>) (context, options) -> "<form>" + options.fn(context) + "</form>"),
        "<form><p>Yehuda</p></form>");
  }

  @Test
  @DisplayName("block helper passing a complex path context")
  public void blockHelperPassingComplexPathContext() throws IOException {
    shouldCompileTo(
        "{{#form yehuda/cat}}<p>{{name}}</p>{{/form}}",
        $("yehuda", $("name", "Yehuda", "cat", $("name", "Harold"))),
        $(
            "form",
            (Helper<Object>) (context, options) -> "<form>" + options.fn(context) + "</form>"),
        "<form><p>Harold</p></form>");
  }

  @Test
  @DisplayName("nested block helpers")
  public void nestedblockhelpers() throws IOException {
    shouldCompileTo(
        "{{#form yehuda}}<p>{{name}}</p>{{#link}}Hello{{/link}}{{/form}}",
        $("yehuda", $("name", "Yehuda")),
        $(
            "link",
            (Helper<Object>)
                (context, options) ->
                    "<a href=\"" + model(options).get("name") + "\">" + options.fn() + "</a>",
            "form",
            (Helper<Object>) (context, options) -> "<form>" + options.fn(context) + "</form>"),
        "<form><p>Yehuda</p><a href=\"Yehuda\">Hello</a></form>");
  }

  @Test
  @DisplayName("block helper inverted sections")
  public void blockHelperInvertedSections() throws IOException {
    String template = "{{#list people}}{{name}}{{^}}<em>Nobody's here</em>{{/list}}";
    Hash helpers =
        $(
            "list",
            (Helper<Object>)
                (context, options) -> {
                  List<?> people = (List<?>) context;
                  if (!people.isEmpty()) {
                    StringBuilder out = new StringBuilder("<ul>");
                    for (Object person : people) {
                      out.append("<li>").append(options.fn(person)).append("</li>");
                    }
                    return out.append("</ul>").toString();
                  }
                  return "<p>" + options.inverse() + "</p>";
                });
    shouldCompileTo(
        template,
        $("people", List.of($("name", "Alan"), $("name", "Yehuda"))),
        helpers,
        "<ul><li>Alan</li><li>Yehuda</li></ul>");
    shouldCompileTo(template, $("people", List.of()), helpers, "<p><em>Nobody's here</em></p>");
    shouldCompileTo(
        "{{#list people}}Hello{{^}}{{message}}{{/list}}",
        $("people", List.of(), "message", "Nobody's here"),
        helpers,
        "<p>Nobody&#x27;s here</p>");
  }

  public record Model03(String helper, Model03 hash) {}

  @Test
  @DisplayName("pathed lambas with parameters")
  public void pathedlambaswithparameters() throws IOException {
    Hash helpers = $("./helper", (Helper<Object>) (context, options) -> "fail");
    shouldCompileTo("{{./helper 1}}", new Model03("winning", null), helpers, "winning");
    shouldCompileTo("{{./helper 1}}", $("helper", "winning"), helpers, "winning");
    shouldCompileTo(
        "{{hash/helper 1}}", new Model03(null, new Model03("winning", null)), helpers, "winning");
    shouldCompileTo("{{hash/helper 1}}", $("hash", $("helper", "winning")), helpers, "winning");
  }

  @Test
  @DisplayName("providing a helpers hash")
  public void providingahelpershash() throws IOException {
    Hash helpers = $("world", (Helper<Object>) (context, options) -> "world");
    shouldCompileTo(
        "Goodbye {{cruel}} {{world}}!", $("cruel", "cruel"), helpers, "Goodbye cruel world!");
    shouldCompileTo(
        "Goodbye {{#iter}}{{cruel}} {{world}}{{/iter}}!",
        $("iter", new Object[] {$("cruel", "cruel")}),
        helpers,
        "Goodbye cruel world!");
  }

  @Test
  @DisplayName("in cases of conflict, helpers win")
  public void incasesofconflicthelperswin() throws IOException {
    Hash helpers = $("search", (Helper<Object>) (context, options) -> "helpers");
    shouldCompileTo("{{{search}}}", $("search", "Explicit"), helpers, "helpers");
    shouldCompileTo("{{search}}", $("search", "Explicit"), helpers, "helpers");
    shouldCompileTo("{{& search}}", $("search", "Explicit"), helpers, "helpers");
  }

  @Test
  @DisplayName("the helpers hash is available is nested contexts")
  public void thehelpershashisavailableisnestedcontexts() throws IOException {
    shouldCompileTo(
        "{{#outer}}{{#inner}}{{helper}}{{/inner}}{{/outer}}",
        $("outer", $("inner", $("unused", new Object[0]))),
        $("helper", (Helper<Object>) (context, options) -> "helper"),
        "helper");
  }

  @Test
  @DisplayName("the helper hash should augment the global hash")
  public void thehelperhashshouldaugmenttheglobalhash() throws IOException {
    shouldCompileTo(
        "{{test_helper}} {{#if cruel}}Goodbye {{cruel}} {{world}}!{{/if}}",
        $("cruel", "cruel"),
        $(
            "test_helper",
            (Helper<Object>) (context, options) -> "found it!",
            "world",
            (Helper<Object>) (context, options) -> "world!"),
        "found it! Goodbye cruel world!!");
  }

  @Test
  @DisplayName("decimal number literals work")
  public void decimalAndNegativeNumberLiteralsWork() throws IOException {
    Helper<Object> hello =
        (context, options) -> "Hello " + context + " " + options.param(0) + " times";
    Helper<Object> num = (context, options) -> "Hello " + context + " times";
    Hash helpers = $("hello", hello, "helloPrimitives", hello, "num", num);
    shouldCompileTo("Message: {{hello -1.2 1.2}}", $, helpers, "Message: Hello -1.2 1.2 times");
    shouldCompileTo(
        "Message: {{helloPrimitives -1.2 1.2}}", $, helpers, "Message: Hello -1.2 1.2 times");
    shouldCompileTo("Message: {{num -12}}", $, helpers, "Message: Hello -12 times");
  }

  @Test
  @DisplayName("simple literals work")
  public void simpleliteralswork() throws IOException {
    shouldCompileTo(
        "Message: {{hello \"world\" 12 true false}}",
        $,
        $(
            "hello",
            (Helper<Object>)
                (context, options) ->
                    "Hello "
                        + context
                        + " "
                        + options.param(0)
                        + " times: "
                        + options.param(1)
                        + " "
                        + options.param(2)),
        "Message: Hello world 12 times: true false");
  }

  @Test
  @DisplayName("using a quote in the middle of a parameter raises an error")
  public void usingaquoteinthemiddleofaparameterraisesanerror() {
    assertThrows(HandlebarsException.class, () -> compile("Message: {{hello wo\"rld\"}}").apply($));
  }

  @Test
  @DisplayName("escaping a String is possible")
  public void escapingaStringispossible() throws IOException {
    shouldCompileTo(
        "Message: {{{hello \"\\\"world\\\"\"}}}",
        $,
        $("hello", (Helper<Object>) (context, options) -> "Hello " + context),
        "Message: Hello \"world\"");
  }

  @Test
  @DisplayName("it works with ' marks")
  public void itworkswithmarks() throws IOException {
    shouldCompileTo(
        "Message: {{{hello \"Alan's world\"}}}",
        $,
        $("hello", (Helper<Object>) (context, options) -> "Hello " + context),
        "Message: Hello Alan's world");
  }

  private static Helper<Object> hashHelper(final boolean block) {
    return (context, options) -> {
      Boolean print = options.hash("print");
      if (print != null) {
        if (print) {
          return "GOODBYE "
              + options.hash("cruel")
              + " "
              + (block ? options.fn() : options.hash("world"));
        }
        return "NOT PRINTING";
      }
      if (block) {
        return "GOODBYE "
            + options.hash("cruel")
            + " "
            + options.fn()
            + " "
            + options.hash("times")
            + " TIMES";
      }
      return "GOODBYE "
          + options.hash("cruel")
          + " "
          + options.hash("world")
          + " "
          + options.hash("times")
          + " TIMES";
    };
  }

  @Test
  @DisplayName("helpers can take an optional hash")
  public void helpersCanTakeAnOptionalHash() throws IOException {
    Hash helpers = $("goodbye", hashHelper(false));
    shouldCompileTo(
        "{{goodbye cruel=\"CRUEL\" world=\"WORLD\" times=12}}",
        $,
        helpers,
        "GOODBYE CRUEL WORLD 12 TIMES");
    shouldCompileTo(
        "{{goodbye cruel=\"CRUEL\" world=\"WORLD\" print=true}}",
        $,
        helpers,
        "GOODBYE CRUEL WORLD");
    shouldCompileTo(
        "{{goodbye cruel=\"CRUEL\" world=\"WORLD\" print=false}}", $, helpers, "NOT PRINTING");
  }

  @Test
  @DisplayName("block helpers can take an optional hash")
  public void blockHelpersCanTakeAnOptionalHash() throws IOException {
    Hash helpers = $("blockGoodbye", hashHelper(true));
    shouldCompileTo(
        "{{#blockGoodbye cruel=\"CRUEL\" times=12}}world{{/blockGoodbye}}",
        $,
        helpers,
        "GOODBYE CRUEL world 12 TIMES");
    shouldCompileTo(
        "{{#blockGoodbye cruel=\"CRUEL\" print=true}}world{{/blockGoodbye}}",
        $,
        helpers,
        "GOODBYE CRUEL world");
    shouldCompileTo(
        "{{#blockGoodbye cruel=\"CRUEL\" print=false}}world{{/blockGoodbye}}",
        $,
        helpers,
        "NOT PRINTING");
  }

  @Test
  @DisplayName("if a context is not found, helperMissing is used")
  public void ifacontextisnotfoundhelperMissingisused() {
    assertThrows(HandlebarsException.class, () -> compile("{{hello}} {{link_to world}}").apply($));
  }

  @Test
  @DisplayName("if a context is not found, custom helperMissing is used")
  public void ifacontextisnotfoundcustomhelperMissingisused() throws IOException {
    shouldCompileTo(
        "{{hello}} {{link_to world}}",
        $("world", "world", "hello", "Hello"),
        $(
            HelperRegistry.HELPER_MISSING,
            (Helper<Object>)
                (context, options) ->
                    options.helperName.equals("link_to")
                        ? new Handlebars.SafeString("<a>" + context + "</a>")
                        : null),
        "Hello <a>world</a>");
  }

  @Test
  @DisplayName("if a value is not found, custom helperMissing is used")
  public void ifavalueisnotfoundcustomhelperMissingisused() throws IOException {
    shouldCompileTo(
        "{{hello}} {{link_to}}",
        $("world", "world", "hello", "Hello"),
        $(
            HelperRegistry.HELPER_MISSING,
            (Helper<Object>)
                (context, options) ->
                    options.helperName.equals("link_to")
                        ? new Handlebars.SafeString("<a>winning</a>")
                        : null),
        "Hello <a>winning</a>");
  }

  @Test
  @DisplayName("helperMissing")
  public void helperMissing() throws IOException {
    Hash helpers =
        $(
            HelperRegistry.HELPER_MISSING,
            (Helper<Object>)
                (context, options) -> {
                  Stream<Object> arguments = Stream.of(options.params);
                  if (options.<Integer>data(Context.PARAM_SIZE) > options.params.length) {
                    arguments = Stream.concat(Stream.of(context), arguments);
                  }
                  String args =
                      arguments.map(Object::toString).collect(Collectors.joining(",", "(", ")"));
                  return new Handlebars.SafeString("Missing: " + options.helperName + args);
                },
            "foo",
            (Helper<Object>)
                (context, options) -> {
                  if (options.tagType == TagType.SECTION) {
                    return "Block Missing: foo(Missing: foo())";
                  }
                  return options
                      .handlebars
                      .helper(HelperRegistry.HELPER_MISSING)
                      .apply(context, options);
                });
    shouldCompileTo("{{#foo}}{{/foo}}", $, helpers, "Block Missing: foo(Missing: foo())");
    shouldCompileTo("{{foo}}", $, helpers, "Missing: foo()");
    shouldCompileTo("{{foo true}}", $, helpers, "Missing: foo(true)");
    shouldCompileTo("{{foo 2 true}}", $, helpers, "Missing: foo(2,true)");
    shouldCompileTo(
        "some_{{foo}}mustache\nsome_{{#foo}}abc{{/foo}}block", $, "some_mustache\nsome_block");
    Map<String, Object> person = $("firstname", "Yehuda", "lastname", "Katz");
    Context context = Context.newContext($("person", person)).data($("person", person));
    shouldCompileTo(
        "{{#person}}\n  {{firstname}} {{lastname}}\n{{/person}}",
        context,
        $(
            "person",
            (Helper<Object>)
                (value, options) -> "Helper 'person' not found. Printing block: " + options.fn()),
        "Helper 'person' not found. Printing block: \n   \n");
  }

  @Test
  @DisplayName("name field")
  public void nameField() throws IOException {
    Hash helpers =
        $(
            HelperRegistry.HELPER_MISSING,
            (Helper<Object>)
                (context, options) ->
                    options.tagType == TagType.SECTION
                            && options.params.length == 0
                            && options.hash.isEmpty()
                        ? "missing: " + options.helperName
                        : "helper missing: " + options.helperName,
            "helper",
            (Helper<Object>) (context, options) -> "ran: " + options.helperName,
            "./helper",
            (Helper<Object>) (context, options) -> "missing: ./helper",
            "foo.helper",
            (Helper<Object>)
                (context, options) ->
                    options.hash.isEmpty() ? "missing: foo.helper" : "helper missing: foo.helper");
    shouldCompileTo("{{helper}}", $, helpers, "ran: helper");
    shouldCompileTo("{{helper 1}}", $, helpers, "ran: helper");
    shouldCompileTo("{{#helper}}{{/helper}}", $, helpers, "ran: helper");
    shouldCompileTo("{{#./helper}}{{/./helper}}", $, helpers, "missing: ./helper");
    shouldCompileTo("{{#helper 1}}{{/helper}}", $, helpers, "ran: helper");
    shouldCompileTo(
        "{{#foo.helper}}{{/foo.helper}}", $("foo", $()), helpers, "missing: foo.helper");
    shouldCompileTo(
        "{{#foo.helper bar=baz}}{{/foo.helper}}",
        $("foo", $()),
        helpers,
        "helper missing: foo.helper");
  }

  @Test
  @DisplayName("name conflicts: helpers take precedence over same-named context properties")
  public void helperstakeprecedenceoversamenamedcontextproperties() throws IOException {
    shouldCompileTo(
        "{{goodbye}} {{cruel world}}",
        $("goodbye", "goodbye", "world", "world"),
        $(
            "goodbye",
            (Helper<Object>)
                (context, options) -> model(options).get("goodbye").toString().toUpperCase(),
            "cruel",
            (Helper<Object>) (context, options) -> "cruel " + context.toString().toUpperCase()),
        "GOODBYE cruel WORLD");
  }

  @Test
  @DisplayName("name conflicts: helpers take precedence over same-named context properties$")
  public void helperstakeprecedenceoversamenamedcontextproperties$() throws IOException {
    shouldCompileTo(
        "{{#goodbye}} {{cruel world}}{{/goodbye}}",
        $("goodbye", "goodbye", "world", "world"),
        $(
            "goodbye",
            (Helper<Object>)
                (context, options) ->
                    model(options).get("goodbye").toString().toUpperCase() + options.fn(),
            "cruel",
            (Helper<Object>) (context, options) -> "cruel " + context.toString().toUpperCase()),
        "GOODBYE cruel WORLD");
  }

  @Test
  @DisplayName("name conflicts: Scoped names take precedence over helpers")
  public void scopednamestakeprecedenceoverhelpers() throws IOException {
    shouldCompileTo(
        "{{this.goodbye}} {{cruel world}} {{cruel this.goodbye}}",
        $("goodbye", "goodbye", "world", "world"),
        $(
            "goodbye",
            (Helper<Object>)
                (context, options) -> model(options).get("goodbye").toString().toUpperCase(),
            "cruel",
            (Helper<Object>) (context, options) -> "cruel " + context.toString().toUpperCase()),
        "goodbye cruel WORLD cruel GOODBYE");
  }

  @Test
  @DisplayName("name conflicts: Scoped names take precedence over block helpers")
  public void Scopednamestakeprecedenceoverblockhelpers() throws IOException {
    shouldCompileTo(
        "{{#goodbye}} {{cruel world}}{{/goodbye}} {{this.goodbye}}",
        $("goodbye", "goodbye", "world", "world"),
        $(
            "goodbye",
            (Helper<Object>)
                (context, options) ->
                    model(options).get("goodbye").toString().toUpperCase() + options.fn(),
            "cruel",
            (Helper<Object>) (context, options) -> "cruel " + context.toString().toUpperCase()),
        "GOODBYE cruel WORLD goodbye");
  }

  @Test
  @DisplayName("block params: should take presedence over context values")
  public void shouldtakepresedenceovercontextvalues() throws IOException {
    shouldCompileTo(
        "{{#goodbyes as |value|}}{{value}}{{/goodbyes}}{{value}}",
        $("value", "foo"),
        $(
            "goodbyes",
            (Helper<Object>)
                (context, options) ->
                    options.apply(options.fn, options.context, Arrays.<Object>asList(1, 2))),
        "1foo");
  }

  @Test
  @DisplayName("block params: should take presedence over context values")
  public void shouldtakepresedenceovercontextvalues2() throws IOException {
    Hash helpers =
        $("goodbyes", (Helper<Object>) (context, options) -> Arrays.asList("a", "b", "c"));
    shouldCompileTo(
        "{{#goodbyes as |value|}}{{value}}{{/goodbyes}}|{{value}}",
        $("value", "foo", "me", "fail"),
        helpers,
        "[a, b, c]|foo");
    shouldCompileTo(
        "{{#goodbyes as |me|}}{{me}}{{/goodbyes}}|{{value}}",
        $("value", "foo", "me", "fail"),
        helpers,
        "[a, b, c]|foo");
    shouldCompileTo(
        "{{#goodbyes as |value|}}{{value}}{{/goodbyes}}|{{value}}",
        $("value", "foo", "goodbyes", new Object[] {"a", "b", "c"}),
        "abc|foo");
  }

  @Test
  @DisplayName("built-in helpers malformed arguments")
  public void builtinhelpersmalformedarguments() {
    // zero arguments means "current context" in this implementation, so only extra arguments fail
    HandlebarsException ex =
        assertThrows(
            HandlebarsException.class,
            () -> compile("{{#if test \"string\"}}{{/if}}").apply($("test", true)));
    assertTrue(ex.getMessage().contains("#if"));
    assertThrows(
        HandlebarsException.class,
        () -> compile("{{#if test null}}{{/if}}").apply($("test", true)));
    assertThrows(
        HandlebarsException.class,
        () -> compile("{{#unless test \"string\"}}{{/unless}}").apply($("test", true)));
    assertThrows(
        HandlebarsException.class,
        () -> compile("{{#unless test null}}{{/unless}}").apply($("test", true)));
    assertThrows(
        HandlebarsException.class,
        () -> compile("{{#with test b}}{{/with}}").apply($("test", true, "b", true)));
    assertThrows(
        HandlebarsException.class,
        () -> compile("{{#each x y}}{{/each}}").apply($("x", List.of(), "y", List.of())));
  }
}
