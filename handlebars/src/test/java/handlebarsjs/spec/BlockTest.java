/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/blocks.js */
@DisplayName("blocks")
public class BlockTest extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.setPrettyPrint(true);
  }

  public static class Goodbye {
    private final String text;

    public Goodbye(final String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }
  }

  public static class Goodbye00 {
    private final String name;

    private final List<Goodbye> goodbyes;

    public Goodbye00(final String name, final List<Goodbye> goodbyes) {
      this.name = name;
      this.goodbyes = goodbyes;
    }

    public String getName() {
      return name;
    }

    public List<Goodbye> getGoodbyes() {
      return goodbyes;
    }
  }

  private static Object[] goodbyes() {
    return new Object[] {$("text", "goodbye"), $("text", "Goodbye"), $("text", "GOODBYE")};
  }

  @Test
  public void arrays() throws IOException {
    String string = "{{#goodbyes}}{{text}}! {{/goodbyes}}cruel {{world}}!";

    shouldCompileTo(
        string,
        $("goodbyes", goodbyes(), "world", "world"),
        "goodbye! Goodbye! GOODBYE! cruel world!");

    shouldCompileTo(string, $("world", "world"), "cruel world!");
  }

  @Test
  @DisplayName("arrays without data")
  public void arraysWithoutData() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{text}}{{/goodbyes}} {{#goodbyes}}{{text}}{{/goodbyes}}",
        $("goodbyes", goodbyes(), "world", "world"),
        "goodbyeGoodbyeGOODBYE goodbyeGoodbyeGOODBYE");
  }

  @Test
  @DisplayName("arrays with index")
  public void arraysWithIndex() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{@index}}. {{text}}! {{/goodbyes}}cruel {{world}}!",
        $("goodbyes", goodbyes(), "world", "world"),
        "0. goodbye! 1. Goodbye! 2. GOODBYE! cruel world!");
  }

  @Test
  @DisplayName("empty block")
  public void emptyBlock() throws IOException {
    String string = "{{#goodbyes}}{{/goodbyes}}cruel {{world}}!";

    shouldCompileTo(string, $("goodbyes", goodbyes(), "world", "world"), "cruel world!");

    shouldCompileTo(string, $("world", "world"), "cruel world!");
  }

  @Test
  @DisplayName("block with complex lookup")
  public void blockWithComplexLookup() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{text}} cruel {{../name}}! {{/goodbyes}}",
        $("name", "Alan", "goodbyes", goodbyes()),
        "goodbye cruel Alan! Goodbye cruel Alan! GOODBYE cruel Alan! ");

    shouldCompileTo(
        "{{#goodbyes}}{{text}} cruel {{../name}}! {{/goodbyes}}",
        new Goodbye00(
            "Alan",
            Arrays.asList(new Goodbye("goodbye"), new Goodbye("Goodbye"), new Goodbye("GOODBYE"))),
        "goodbye cruel Alan! Goodbye cruel Alan! GOODBYE cruel Alan! ");

    shouldCompileTo("{{foo}}", $(), "");
  }

  @Test
  @DisplayName("multiple blocks with complex lookup")
  public void multipleBlocksWithComplexLookup() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{../name}}{{../name}}{{/goodbyes}}",
        $("name", "Alan", "goodbyes", goodbyes()),
        "AlanAlanAlanAlanAlanAlan");

    shouldCompileTo(
        "{{#goodbyes}}{{../name}}{{../name}}{{/goodbyes}}",
        new Goodbye00(
            "Alan",
            Arrays.asList(new Goodbye("goodbye"), new Goodbye("Goodbye"), new Goodbye("GOODBYE"))),
        "AlanAlanAlanAlanAlanAlan");
  }

  @Test
  @DisplayName("block with complex lookup using nested context")
  public void blockWithComplexLookupUsingNestedContext() throws IOException {
    assertThrows(
        HandlebarsException.class,
        () -> compile("{{#goodbyes}}{{text}} cruel {{foo/../name}}! {{/goodbyes}}").apply($()));
  }

  @Test
  @DisplayName("block with deep nested complex lookup")
  public void blockWithDeepNestedComplexLookup() throws IOException {
    shouldCompileTo(
        "{{#outer}}Goodbye {{#inner}}cruel {{../sibling}} {{../../omg}}{{/inner}}{{/outer}}",
        $(
            "omg",
            "OMG!",
            "outer",
            new Object[] {$("sibling", "sad", "inner", new Object[] {$("text", "goodbye")})}),
        "Goodbye cruel sad OMG!");
  }

  @Test
  @DisplayName("works with cached blocks")
  public void worksWithCachedBlocks() throws IOException {
    shouldCompileTo(
        "{{#each person}}{{#with .}}{{first}} {{last}}{{/with}}{{/each}}",
        $(
            "person",
            new Object[] {
              $("first", "Alan", "last", "Johnson"), $("first", "Alan", "last", "Johnson")
            }),
        "Alan JohnsonAlan Johnson");
  }

  @Test
  @DisplayName("inverted sections with unset value")
  public void invertedSectionWithUnsetValue() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{this}}{{/goodbyes}}{{^goodbyes}}Right On!{{/goodbyes}}", $(), "Right On!");
  }

  @Test
  @DisplayName("inverted section with false value")
  public void invertedSectionWithFalseValue() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{this}}{{/goodbyes}}{{^goodbyes}}Right On!{{/goodbyes}}",
        $("goodbyes", false),
        "Right On!");
  }

  @Test
  @DisplayName("inverted section with empty set")
  public void invertedSectionWithEmptySet() throws IOException {
    shouldCompileTo(
        "{{#goodbyes}}{{this}}{{/goodbyes}}{{^goodbyes}}Right On!{{/goodbyes}}",
        $("goodbyes", new Object[0]),
        "Right On!");
  }

  @Test
  @DisplayName("block inverted sections")
  public void blockInvertedSections() throws IOException {
    shouldCompileTo(
        "{{#people}}{{name}}{{^}}{{none}}{{/people}}", $("none", "No people"), "No people");
  }

  @Test
  @DisplayName("chained inverted sections")
  public void chainedInvertedSections() throws IOException {
    shouldCompileTo(
        "{{#people}}{{name}}{{else if none}}{{none}}{{/people}}",
        $("none", "No people"),
        "No people");

    shouldCompileTo(
        "{{#people}}{{name}}{{else if nothere}}fail{{else unless nothere}}{{none}}{{/people}}",
        $("none", "No people"),
        "No people");

    shouldCompileTo(
        "{{#people}}{{name}}{{else if none}}{{none}}{{else}}fail{{/people}}",
        $("none", "No people"),
        "No people");
  }

  @Test
  @DisplayName("chained inverted sections with mismatch")
  public void chainedInvertedSectionsWithMismatch() throws IOException {
    assertThrows(
        HandlebarsException.class,
        () ->
            compile("{{#people}}{{name}}{{else if none}}{{none}}{{/if}}")
                .apply($("none", "No people")));
  }

  @Test
  @DisplayName("block inverted sections with empty arrays")
  public void blockInvertedSectionsWithEmptyArrays() throws IOException {
    shouldCompileTo(
        "{{#people}}{{name}}{{^}}{{none}}{{/people}}",
        $("none", "No people", "people", new Object[0]),
        "No people");
  }

  @Test
  @DisplayName("block standalone else sections")
  public void blockstandaloneelsesections() throws IOException {
    shouldCompileTo(
        "{{#people}}\n{{name}}\n{{^}}\n{{none}}\n{{/people}}\n",
        $("none", "No people"),
        "No people\n");

    shouldCompileTo(
        "{{#none}}\n{{.}}\n{{^}}\n{{none}}\n{{/none}}\n", $("none", "No people"), "No people\n");

    shouldCompileTo(
        "{{#people}}\n{{name}}\n{{^}}\n{{none}}\n{{/people}}\n",
        $("none", "No people"),
        "No people\n");
  }

  @Test
  @DisplayName("block standalone else sections can be disabled")
  public void blockstandaloneelsesectionscanbedisabled() throws IOException {
    // without prettyPrint standalone lines are kept (handlebars.js ignoreStandalone)
    Handlebars handlebars = new Handlebars();
    assertEquals(
        "\nNo people\n\n",
        handlebars
            .compileInline("{{#people}}\n{{name}}\n{{^}}\n{{none}}\n{{/people}}\n")
            .apply($("none", "No people")));

    assertEquals(
        "\nNo people\n\n",
        handlebars
            .compileInline("{{#none}}\n{{.}}\n{{^}}\nFail\n{{/none}}\n")
            .apply($("none", "No people")));
  }

  @Test
  @DisplayName("block standalone chained else sections")
  public void block_standalone_chained_else_sections() throws IOException {
    shouldCompileTo(
        "{{#people}}\n{{name}}\n{{else if none}}\n{{none}}\n{{/people}}\n",
        $("none", "No people"),
        "No people\n");

    shouldCompileTo(
        "{{#people}}\n{{name}}\n{{else if none}}\n{{none}}\n{{^}}\n{{/people}}\n",
        $("none", "No people"),
        "No people\n");
  }

  @Test
  @DisplayName("should handle nesting")
  public void should_handle_nesting() throws IOException {
    shouldCompileTo(
        "{{#data}}\n{{#if true}}\n{{.}}\n{{/if}}\n{{/data}}\nOK.",
        $("data", new Object[] {1, 3, 5}),
        "1\n3\n5\nOK.");
  }

  @Test
  @DisplayName("block with deep recursive lookup lookup")
  public void blockwithdeeprecursivelookuplookup() throws IOException {
    shouldCompileTo(
        "{{#outer}}Goodbye {{#inner}}cruel {{omg}}{{/inner}}{{/outer}}",
        $("omg", "OMG!", "outer", new Object[] {$("inner", new Object[] {$("text", "goodbye")})}),
        "Goodbye cruel OMG!");
  }

  @Test
  @DisplayName("block with deep recursive pathed lookup")
  public void blockwithdeeprecursivepathedlookup() throws IOException {
    shouldCompileTo(
        "{{#outer}}Goodbye {{#inner}}cruel {{omg.yes}}{{/inner}}{{/outer}}",
        $(
            "omg",
            $("yes", "OMG!"),
            "outer",
            new Object[] {$("inner", new Object[] {$("yes", "no", "text", "goodbye")})}),
        "Goodbye cruel OMG!");
  }
}
