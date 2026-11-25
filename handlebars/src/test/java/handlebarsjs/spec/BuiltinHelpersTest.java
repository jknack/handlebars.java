/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/builtins.js */
@DisplayName("builtins")
public class BuiltinHelpersTest extends AbstractTest {

  @Test
  @DisplayName("if")
  public void ifHelper() throws IOException {
    String template = "{{#if goodbye}}GOODBYE {{/if}}cruel {{world}}!";
    shouldCompileTo(template, $("goodbye", true, "world", "world"), "GOODBYE cruel world!");
    shouldCompileTo(template, $("goodbye", "dummy", "world", "world"), "GOODBYE cruel world!");
    shouldCompileTo(template, $("goodbye", false, "world", "world"), "cruel world!");
    shouldCompileTo(template, $("world", "world"), "cruel world!");
    shouldCompileTo(template, $("goodbye", new String[0], "world", "world"), "cruel world!");
    shouldCompileTo(template, $("goodbye", 0, "world", "world"), "cruel world!");
  }

  public record If00(boolean goodbye, String world) {}

  public record If01(String world) {
    public String goodbye() {
      return world();
    }
  }

  public record If02(String world) {
    public String goodbye() {
      return null;
    }
  }

  @Test
  @DisplayName("if with function argument")
  public void ifwithfunctionargument() throws IOException {
    String template = "{{#if goodbye}}GOODBYE {{/if}}cruel {{world}}!";
    shouldCompileTo(template, new If00(true, "world"), "GOODBYE cruel world!");
    shouldCompileTo(template, new If01("world"), "GOODBYE cruel world!");
    shouldCompileTo(template, new If00(false, "world"), "cruel world!");
    shouldCompileTo(template, new If02("world"), "cruel world!");
  }

  public record If03(boolean goodbye) {}

  public record If04(String world, If03 foo) {}

  @Test
  @DisplayName("should not change the depth list")
  public void shouldnotchangethedepthlist() throws IOException {
    shouldCompileTo(
        "{{#with foo}}{{#if goodbye}}GOODBYE cruel {{../world}}!{{/if}}{{/with}}",
        $("foo", $("goodbye", true), "world", "world"),
        "GOODBYE cruel world!");
    shouldCompileTo(
        "{{#with foo}}{{#if goodbye}}GOODBYE cruel {{../world}}!{{/if}}{{/with}}",
        new If04("world", new If03(true)),
        "GOODBYE cruel world!");
  }

  @Test
  @DisplayName("with")
  public void withHelper() throws IOException {
    shouldCompileTo(
        "{{#with person}}{{first}} {{last}}{{/with}}",
        $("person", $("first", "Alan", "last", "Johnson")),
        "Alan Johnson");
  }

  @Test
  @DisplayName("with with else")
  public void withWithElse() throws IOException {
    shouldCompileTo(
        "{{#with person}}Person is present{{else}}Person is not present{{/with}}",
        null,
        "Person is not present");
  }

  public record With00(String first, String last) {}

  public record With01(With00 person) {}

  @Test
  @DisplayName("with provides block parameter")
  public void withprovidesblockparameter() throws IOException {
    String template = "{{#with person as |foo|}}{{foo.first}} {{last}}{{/with}}";
    shouldCompileTo(template, $("person", $("first", "Alan", "last", "Johnson")), "Alan Johnson");
    shouldCompileTo(template, new With01(new With00("Alan", "Johnson")), "Alan Johnson");
  }

  @Test
  public void each() throws IOException {
    String template = "{{#each goodbyes}}{{text}}! {{/each}}cruel {{world}}!";
    shouldCompileTo(
        template,
        $(
            "goodbyes",
            List.of($("text", "goodbye"), $("text", "Goodbye"), $("text", "GOODBYE")),
            "world",
            "world"),
        "goodbye! Goodbye! GOODBYE! cruel world!");
    shouldCompileTo(template, $("goodbyes", List.of(), "world", "world"), "cruel world!");
    shouldCompileTo(template, null, "cruel !");
  }

  @Test
  public void eachEvaluatesIterableOnce() throws IOException {
    List<Object> calls = new ArrayList<>();
    Object input = $("title", "outer");
    shouldCompileTo(
        "{{#each (items)}}{{this}}{{/each}}",
        input,
        $(
            "items",
            (Helper<Object>)
                (context, options) -> {
                  calls.add(options.context.model());
                  return List.of("a", "b");
                }),
        "ab");
    assertEquals(List.of(input), calls);
  }

  @Test
  public void eachEvaluatesHashArguments() throws IOException {
    List<String> calls = new ArrayList<>();
    shouldCompileTo(
        "{{#each this ignored=(hashValue)}}{{this}}{{/each}}",
        List.of(1, 2),
        $(
            "hashValue",
            (Helper<Object>)
                (context, options) -> {
                  calls.add("hash");
                  return "ignored";
                }),
        "12",
        "");
    assertEquals(List.of("hash"), calls);
  }

  @Test
  public void eachDoesNotFormatItsNullReturn() throws IOException {
    String template = "{{#each values}}{{this}}{{else}}empty:{{title}}{{/each}}";
    assertWithNullFormatter(template, $("values", List.of("a", "b"), "title", "outer"), "ab");
    assertWithNullFormatter(template, $("values", List.of(), "title", "outer"), "empty:outer");
  }

  @Test
  public void eachCanBeOverriddenByHelperProvider() throws IOException {
    shouldCompileTo(
        "{{#each values}}builtin{{else}}{{title}}{{/each}}",
        $("values", List.of(1, 2), "title", "outer:"),
        $("each", (Helper<Object>) (context, options) -> options.inverse() + "custom"),
        "outer:custom",
        "");
  }

  @Test
  public void conditionalsPassContextToPredicates() throws IOException {
    List<Object> contexts = new ArrayList<>();
    Predicate<Object> admin =
        context -> {
          contexts.add(context);
          return true;
        };
    Object input = $("admin", admin, "title", "outer");
    Helper<Object> ifHelper =
        (context, options) ->
            admin.test(options.context.model()) ? options.fn() : options.inverse();
    Helper<Object> unlessHelper =
        (context, options) ->
            admin.test(options.context.model()) ? options.inverse() : options.fn();
    shouldCompileTo(
        "{{#if admin}}if:{{title}} {{/if}}"
            + "{{#unless admin}}hidden{{else}}unless:{{title}}{{/unless}}",
        input,
        $("if", ifHelper, "unless", unlessHelper),
        "if:outer unless:outer",
        "");
    assertEquals(List.of(input, input), contexts);
  }

  @Test
  public void conditionalsHonorIncludeZero() throws IOException {
    shouldCompileTo(
        "{{#if zero includeZero=true}}if{{/if}}|"
            + "{{#unless zero includeZero=true}}hidden{{else}}unless{{/unless}}",
        $("zero", 0),
        "if|unless");
  }

  @Test
  public void conditionalsEvaluateHashArguments() throws IOException {
    List<String> calls = new ArrayList<>();
    shouldCompileTo(
        "{{#if this ignored=(hashValue)}}if{{/if}}"
            + "{{#unless this ignored=(hashValue)}}{{else}}unless{{/unless}}"
            + "{{#with this ignored=(hashValue)}}{{title}}{{/with}}",
        $("title", "with"),
        $(
            "hashValue",
            (Helper<Object>)
                (context, options) -> {
                  calls.add("hash");
                  return "ignored";
                }),
        "ifunlesswith",
        "");
    assertEquals(List.of("hash", "hash", "hash"), calls);
  }

  @Test
  public void invertedConditionalsRenderTheirBody() throws IOException {
    shouldCompileTo(
        "{{^if flag}}if{{/if}}|{{^unless flag}}unless{{/unless}}|"
            + "{{^with person}}{{title}}{{/with}}",
        $("flag", false, "title", "with"),
        "if||with");
  }

  @Test
  public void withBlockParamsKeepCurrentData() throws IOException {
    shouldCompileTo(
        "{{#each people}}{{#with this as |person|}}{{@index}}:{{person.name}} {{/with}}{{/each}}",
        $("people", List.of($("name", "Alan"), $("name", "Yehuda"))),
        "0:Alan 1:Yehuda ");
  }

  @Test
  public void blockHelpersDoNotFormatTheirNullReturn() throws IOException {
    String template =
        "{{#if flag}}if{{/if}}|{{#unless flag}}{{else}}unless{{/unless}}|"
            + "{{#with person}}{{name}}{{else}}{{title}}{{/with}}|{{#eq left right}}eq{{/eq}}";
    Helper<Object> eq =
        (context, options) ->
            Objects.equals(context, options.param(0)) ? options.fn() : options.inverse();
    assertWithNullFormatter(
        template,
        $("flag", true, "person", $("name", "Alan"), "title", "outer", "left", 1, "right", 1),
        "if|unless|Alan|eq",
        eq);
    assertWithNullFormatter(
        template, $("flag", false, "title", "outer", "left", 1, "right", 2), "||outer|", eq);
  }

  @Test
  public void conditionalsCanBeOverriddenByHelperProvider() throws IOException {
    shouldCompileTo(
        "{{#if flag}}builtin{{/if}} {{#unless flag}}builtin{{/unless}} "
            + "{{#with flag}}builtin{{/with}}",
        $("flag", true),
        $(
            "if", (Helper<Object>) (context, options) -> "if:" + context,
            "unless", (Helper<Object>) (context, options) -> "unless:" + context,
            "with", (Helper<Object>) (context, options) -> "with:" + context),
        "if:true unless:true with:true",
        "");
  }

  public record Each00(String text) {}

  public record Each01(Each00 first, Each00 second) {}

  public record Each02(Each01 goodbyes, String world) {}

  public record Each03(int intValue, String stringValue, Each00 bean) {}

  @Test
  @DisplayName("each with an object and @key")
  public void eachwithanobjectandkey() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}{{@key}}. {{text}}! {{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            $("<b>#1</b>", $("text", "goodbye"), "2", $("text", "GOODBYE")),
            "world",
            "world"),
        "&lt;b&gt;#1&lt;/b&gt;. goodbye! 2. GOODBYE! cruel world!");
    shouldCompileToEither(
        "{{#each goodbyes}}{{@key}}. {{text}}! {{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            $("first", $("text", "goodbye"), "second", $("text", "GOODBYE")),
            "world",
            "world"),
        "first. goodbye! second. GOODBYE! cruel world!",
        "second. GOODBYE! cruel world! first. goodbye!");
  }

  @Test
  public void eachwithanobjectandkeyJavaBeans() throws IOException {
    shouldCompileToEither(
        "{{#each goodbyes}}{{@key}}. {{text}}! {{/each}}cruel {{world}}!",
        new Each02(new Each01(new Each00("goodbye"), new Each00("GOODBYE")), "world"),
        "first. goodbye! second. GOODBYE! cruel world!",
        "second. GOODBYE! cruel world! first. goodbye!");
    assertEachOnEach03();
    shouldCompileToEither(
        "{{#each goodbyes}}{{@key}}. {{text}}! {{/each}}cruel {{world}}!",
        new Each02(new Each01(new Each00("goodbye"), new Each00("GOODBYE")), "world"),
        "first. goodbye! second. GOODBYE! cruel world!",
        "second. GOODBYE! cruel world! first. goodbye!");
    assertEachOnEach03();
  }

  @Test
  @DisplayName("each with @index")
  public void eachWithIndex() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}{{@index}}. {{text}}! {{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            List.of($("text", "goodbye"), $("text", "Goodbye"), $("text", "GOODBYE")),
            "world",
            "world"),
        "0. goodbye! 1. Goodbye! 2. GOODBYE! cruel world!");
  }

  @Test
  @DisplayName("each with nested @index")
  public void eachWithNestedIndex() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}{{@index}}. {{text}}! {{#each ../goodbyes}}{{@index}} {{/each}}After"
            + " {{@index}} {{/each}}{{@index}}cruel {{world}}!",
        $(
            "goodbyes",
            List.of($("text", "goodbye"), $("text", "Goodbye"), $("text", "GOODBYE")),
            "world",
            "world"),
        "0. goodbye! 0 1 2 After 0 1. Goodbye! 0 1 2 After 1 2. GOODBYE! 0 1 2 After 2 cruel"
            + " world!");
  }

  public record Each04(List<Each00> goodbyes, String world) {}

  @Test
  @DisplayName("each with block params")
  public void eachWithBlockParams() throws IOException {
    String template =
        "{{#each goodbyes as |value index|}}{{index}}. {{value.text}}! {{#each ../goodbyes as"
            + " |childValue childIndex|}} {{index}} {{childIndex}}{{/each}} After {{index}}"
            + " {{/each}}{{index}}cruel {{world}}!";
    String expected = "0. goodbye!  0 0 0 1 After 0 1. Goodbye!  1 0 1 1 After 1 cruel world!";
    shouldCompileTo(
        template,
        $("goodbyes", List.of($("text", "goodbye"), $("text", "Goodbye")), "world", "world"),
        expected);
    shouldCompileTo(
        template,
        new Each04(List.of(new Each00("goodbye"), new Each00("Goodbye")), "world"),
        expected);
  }

  @Test
  @DisplayName("each object with @index")
  public void eachObjectWithIndex() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}{{@index}}. {{text}}! {{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            $("a", new Each00("goodbye"), "b", new Each00("Goodbye"), "c", new Each00("GOODBYE")),
            "world",
            "world"),
        "0. goodbye! 1. Goodbye! 2. GOODBYE! cruel world!");
  }

  @Test
  @DisplayName("each with @first")
  public void eachwithfirst() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}{{#if @first}}{{text}}! {{/if}}{{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            List.of(new Each00("goodbye"), new Each00("Goodbye"), new Each00("GOODBYE")),
            "world",
            "world"),
        "goodbye! cruel world!");
  }

  @Test
  @DisplayName("each with nested @first")
  public void eachwithnestedfirst() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}({{#if @first}}{{text}}! {{/if}}{{#each ../goodbyes}}{{#if"
            + " @first}}{{text}}!{{/if}}{{/each}}{{#if @first}} {{text}}!{{/if}})"
            + " {{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            List.of(new Each00("goodbye"), new Each00("Goodbye"), new Each00("GOODBYE")),
            "world",
            "world"),
        "(goodbye! goodbye! goodbye!) (goodbye!) (goodbye!) cruel world!");
  }

  @Test
  @DisplayName("each object with @first")
  public void eachobjectwithfirst() throws IOException {
    String template = "{{#each goodbyes}}{{#if @first}}{{text}}! {{/if}}{{/each}}cruel {{world}}!";
    Object input =
        $("goodbyes", List.of(new Each00("goodbye"), new Each00("Goodbye")), "world", "world");
    shouldCompileTo(template, input, "goodbye! cruel world!");
    shouldCompileTo(
        template,
        new Each04(List.of(new Each00("goodbye"), new Each00("Goodbye")), "world"),
        "goodbye! cruel world!");
  }

  @Test
  @DisplayName("each with @last")
  public void eachwithlast() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}{{#if @last}}{{text}}! {{/if}}{{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            $("foo", $("text", "goodbye"), "bar", $("text", "Goodbye")),
            "world",
            "world"),
        "Goodbye! cruel world!");
  }

  @Test
  @DisplayName("each with nested @last")
  public void eachwithnestedlast() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}({{#if @last}}{{text}}! {{/if}}{{#each ../goodbyes}}{{#if"
            + " @last}}{{text}}!{{/if}}{{/each}}{{#if @last}} {{text}}!{{/if}}) {{/each}}cruel"
            + " {{world}}!",
        $(
            "goodbyes",
            List.of(new Each00("goodbye"), new Each00("Goodbye"), new Each00("GOODBYE")),
            "world",
            "world"),
        "(GOODBYE!) (GOODBYE!) (GOODBYE! GOODBYE! GOODBYE!) cruel world!");
  }

  @Test
  @DisplayName("each object when last key is an empty string")
  public void eachobjectwhenlastkeyisanemptystring() throws IOException {
    shouldCompileTo(
        "{{#each goodbyes}}{{@index}}. {{text}}! {{/each}}cruel {{world}}!",
        $(
            "goodbyes",
            $("a", $("text", "goodbye"), "b", $("text", "Goodbye"), "", $("text", "GOODBYE")),
            "world",
            "world"),
        "0. goodbye! 1. Goodbye! 2. GOODBYE! cruel world!");
  }

  @Test
  @DisplayName("data passed to helpers")
  public void datapassedtohelpers() throws IOException {
    Template template =
        compile(
            "{{#each letters}}{{this}}{{detectDataInsideEach}}{{/each}}",
            $(
                "detectDataInsideEach",
                (Helper<Object>) (context, options) -> options.data("exclaim")));
    Context context = Context.newContext($("letters", new String[] {"a", "b", "c"}));
    context.data("exclaim", "!");
    assertEquals("a!b!c!", template.apply(context));
  }

  public record EachMap00(int one, int two, int three) {}

  public record EachMap(EachMap00 map) {}

  @Test
  @DisplayName("each on Map")
  public void eachonMap() throws IOException {
    String template = "{{#each map}}{{@key}}(i{{@index}}) {{.}} {{/each}}";
    shouldCompileTo(
        template,
        $("map", $("1", "one", "2", "two", "3", "three")),
        "1(i0) one 2(i1) two 3(i2) three ");
    shouldCompileTo(
        "{{#each map}}{{#if @first}}{{.}}{{/if}}{{/each}}",
        $("map", $("1", "one", "2", "two", "3", "three")),
        "one");
    shouldCompileTo(
        "{{#each map}}{{#if @last}}{{.}}{{/if}}{{/each}}",
        $("map", $("1", "one", "2", "two", "3", "three")),
        "three");
    shouldCompileTo("{{#each map}}{{.}}{{/each}}not-in-each", $("map", $()), "not-in-each");
  }

  @Test
  public void eachonMapJavaBeans() throws IOException {
    // reflection doesn't guarantee the member order, so only check order-independent facts
    Object model = new EachMap(new EachMap00(1, 2, 3));
    List<String> items =
        List.of(
            compile("{{#each map}}{{@key}}(i{{@index}}) {{.}}|{{/each}}")
                .apply(model)
                .split("\\|"));
    assertEquals(3, items.size());
    assertEquals(
        List.of("one 1", "three 3", "two 2"),
        items.stream().map(it -> it.replaceAll("\\(i\\d\\)", "")).sorted().toList());
    assertEquals(
        List.of("(i0)", "(i1)", "(i2)"),
        items.stream().map(it -> it.replaceAll("^\\w+(\\(i\\d\\)).*$", "$1")).sorted().toList());

    List<String> flags =
        List.of(
            compile("{{#each map}}{{#if @first}}F{{/if}}{{#if @last}}L{{/if}}{{.}}|{{/each}}")
                .apply(model)
                .split("\\|"));
    assertEquals(3, flags.size());
    assertTrue(flags.get(0).startsWith("F"), flags.toString());
    assertTrue(flags.get(2).startsWith("L"), flags.toString());
    assertEquals(1, flags.stream().filter(it -> it.contains("F")).count());
    assertEquals(1, flags.stream().filter(it -> it.contains("L")).count());

    shouldCompileTo("{{#each map}}{{.}}{{/each}}not-in-each", new EachMap(null), "not-in-each");
  }

  @Test
  @DisplayName("each on Set")
  public void eachOnSet() throws IOException {
    Set<Integer> set = new TreeSet<>(List.of(1, 2, 3));
    shouldCompileTo(
        "{{#each set}}{{@key}}(i{{@index}}) {{.}} {{/each}}",
        $("set", set),
        "0(i0) 1 1(i1) 2 2(i2) 3 ");
    shouldCompileTo("{{#each set}}{{#if @first}}{{.}}{{/if}}{{/each}}", $("set", set), "1");
    shouldCompileTo("{{#each set}}{{#if @last}}{{.}}{{/if}}{{/each}}", $("set", set), "3");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", Set.of()), "not-in-each");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", null), "not-in-each");
  }

  @Test
  @DisplayName("each on List")
  public void eachOnList() throws IOException {
    List<Integer> list = List.of(1, 2, 3);
    shouldCompileTo(
        "{{#each set}}{{@key}}(i{{@index}}) {{.}} {{/each}}",
        $("set", list),
        "0(i0) 1 1(i1) 2 2(i2) 3 ");
    shouldCompileTo("{{#each set}}{{#if @first}}{{.}}{{/if}}{{/each}}", $("set", list), "1");
    shouldCompileTo("{{#each set}}{{#if @last}}{{.}}{{/if}}{{/each}}", $("set", list), "3");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", Set.of()), "not-in-each");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", null), "not-in-each");
  }

  @Test
  @DisplayName("each on Iterable")
  public void eachOnIterable() throws IOException {
    Iterable<Integer> iterable = () -> List.of(1, 2, 3).iterator();
    shouldCompileTo(
        "{{#each set}}{{@key}}(i{{@index}}) {{.}} {{/each}}",
        $("set", iterable),
        "0(i0) 1 1(i1) 2 2(i2) 3 ");
    shouldCompileTo("{{#each set}}{{#if @first}}{{.}}{{/if}}{{/each}}", $("set", iterable), "1");
    shouldCompileTo("{{#each set}}{{#if @last}}{{.}}{{/if}}{{/each}}", $("set", iterable), "3");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", Set.of()), "not-in-each");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", null), "not-in-each");
  }

  @Test
  @DisplayName("each on Iterator")
  public void eachOnIterator() throws IOException {
    List<Integer> values = List.of(1, 2, 3);
    shouldCompileTo(
        "{{#each set}}{{@key}}(i{{@index}}) {{.}} {{/each}}",
        $("set", values.iterator()),
        "0(i0) 1 1(i1) 2 2(i2) 3 ");
    shouldCompileTo(
        "{{#each set}}{{#if @first}}{{.}}{{/if}}{{/each}}", $("set", values.iterator()), "1");
    shouldCompileTo(
        "{{#each set}}{{#if @last}}{{.}}{{/if}}{{/each}}", $("set", values.iterator()), "3");
    shouldCompileTo(
        "{{#each set}}{{.}}{{/each}}not-in-each",
        $("set", Collections.emptyIterator()),
        "not-in-each");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", null), "not-in-each");
  }

  @Test
  @DisplayName("each on primitive arrays")
  public void eachOnPrimitiveArray() throws IOException {
    int[] values = {1, 2, 3};
    shouldCompileTo(
        "{{#each set}}{{@key}}(i{{@index}}) {{.}} {{/each}}",
        $("set", values),
        "0(i0) 1 1(i1) 2 2(i2) 3 ");
    shouldCompileTo("{{#each set}}{{#if @first}}{{.}}{{/if}}{{/each}}", $("set", values), "1");
    shouldCompileTo("{{#each set}}{{#if @last}}{{.}}{{/if}}{{/each}}", $("set", values), "3");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", Set.of()), "not-in-each");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", null), "not-in-each");
  }

  @Test
  @DisplayName("each on arrays")
  public void eachOnArray() throws IOException {
    Object[] values = {1, 2, 3};
    shouldCompileTo(
        "{{#each set}}{{@key}}(i{{@index}}) {{.}} {{/each}}",
        $("set", values),
        "0(i0) 1 1(i1) 2 2(i2) 3 ");
    shouldCompileTo("{{#each set}}{{#if @first}}{{.}}{{/if}}{{/each}}", $("set", values), "1");
    shouldCompileTo("{{#each set}}{{#if @last}}{{.}}{{/if}}{{/each}}", $("set", values), "3");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", Set.of()), "not-in-each");
    shouldCompileTo("{{#each set}}{{.}}{{/each}}not-in-each", $("set", null), "not-in-each");
  }

  // reflection doesn't guarantee the member order
  private void assertEachOnEach03() throws IOException {
    String result =
        compile("{{#each this}}{{@key}}: {{{.}}} {{/each}}")
            .apply(new Each03(43, "world", new Each00("...")));
    List<String> actual = new ArrayList<>(List.of(result.trim().split(" (?=[a-zA-Z]+: )")));
    Collections.sort(actual);
    assertEquals(List.of("bean: Each00[text=...]", "intValue: 43", "stringValue: world"), actual);
  }

  private void shouldCompileToEither(String template, Object data, String first, String second)
      throws IOException {
    String result = compile(template).apply(data);
    assertTrue(first.equals(result) || second.equals(result), "Unexpected output: " + result);
  }

  private void assertWithNullFormatter(String template, Object data, String expected)
      throws IOException {
    assertWithNullFormatter(template, data, expected, null);
  }

  private void assertWithNullFormatter(
      String template, Object data, String expected, Helper<Object> eq) throws IOException {
    Handlebars handlebars =
        new Handlebars().with((value, next) -> value == null ? "[null]" : next.format(value));
    if (eq != null) {
      handlebars.registerHelper("eq", eq);
    }
    Template compiled = handlebars.compileInline(template);
    assertEquals(expected, compiled.apply(data));
  }
}
