/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/regressions.js */
@DisplayName("regressions")
public class RegressionsTest extends AbstractTest {

  private boolean prettyPrint;

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.setPrettyPrint(prettyPrint);
  }

  public static class Author {
    private final String name;

    public Author(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static class Book {
    private final String title;

    private final Author author;

    public Book(final String title, final Author author) {
      this.title = title;
      this.author = author;
    }

    public String getTitle() {
      return title;
    }

    public Author getAuthor() {
      return author;
    }
  }

  public static class Books {
    private final List<Book> books;

    public Books(final List<Book> books) {
      this.books = books;
    }

    public List<Book> getBooks() {
      return books;
    }
  }

  @Test
  @DisplayName("Cannot read property of undefined")
  public void cannotreadpropertyofundefined() throws IOException {
    shouldCompileTo(
        "{{#books}}{{title}}{{author.name}}{{/books}}",
        $(
            "books",
            List.of(
                $("title", "The origin of species", "author", $("name", "Charles Darwin")),
                $("title", "Lazarillo de Tormes"))),
        "The origin of speciesCharles DarwinLazarillo de Tormes");

    shouldCompileTo(
        "{{#books}}{{title}}{{author.name}}{{/books}}",
        new Books(
            List.of(
                new Book("The origin of species", new Author("Charles Darwin")),
                new Book("Lazarillo de Tormes", null))),
        "The origin of speciesCharles DarwinLazarillo de Tormes");
  }

  @Test
  @DisplayName("Inverted sections print when they shouldn't")
  public void invertedsectionsprintwhentheyshouldnt() throws IOException {
    String string = "{{^set}}not set{{/set}} :: {{#set}}set{{/set}}";

    shouldCompileTo(string, $, "not set :: ");

    shouldCompileTo(string, $("set", null), "not set :: ");

    shouldCompileTo(string, $("set", false), "not set :: ");

    shouldCompileTo(string, $("set", true), " :: set");
  }

  public static class ArrayLike {
    private final int[] arr;

    public ArrayLike(final int[] arr) {
      this.arr = arr;
    }

    public int[] getArr() {
      return arr;
    }
  }

  public static class ListLike {
    private final List<Integer> arr;

    public ListLike(final List<Integer> arr) {
      this.arr = arr;
    }

    public List<Integer> getArr() {
      return arr;
    }
  }

  @Test
  @DisplayName("Using array index twice, breaks the template")
  public void Usingarrayindextwicebreaksthetemplate() throws IOException {
    shouldCompileTo("{{arr.[0]}}, {{arr.[1]}}", new ListLike(List.of(1, 2)), "1, 2");

    shouldCompileTo("{{arr.[0]}}, {{arr.[1]}}", new ArrayLike(new int[] {1, 2}), "1, 2");

    shouldCompileTo("{{arr.[0]}}, {{arr.[1]}}", $("arr", List.of(1, 2)), "1, 2");
  }

  public static class Thing {
    private final String word;

    private final String className;

    public Thing(final String word, final String className) {
      this.word = word;
      this.className = className;
    }

    public String getWord() {
      return word;
    }

    public String getClassName() {
      return className;
    }
  }

  public static class Things {
    private final List<Thing> things;

    private final boolean hasThings;

    public Things(final List<Thing> things, final boolean hasThings) {
      this.things = things;
      this.hasThings = hasThings;
    }

    public List<Thing> getThings() {
      return things;
    }

    public boolean isHasThings() {
      return hasThings;
    }

    public String getThing() {
      return "blah";
    }
  }

  @Test
  @DisplayName("lambdas weren't being properly resolved")
  public void lambdaswerentbeingproperlyresolved() throws IOException {
    prettyPrint = true;
    String string =
        "<strong>This is a slightly more complicated {{thing}}.</strong>.\n"
            + "{{! Just ignore this business. }}\n"
            + "Check this out:\n"
            + "{{#hasThings}}\n"
            + "<ul>\n"
            + "{{#things}}\n"
            + "<li class={{className}}>{{word}}</li>\n"
            + "{{/things}}</ul>.\n"
            + "{{/hasThings}}\n"
            + "{{^hasThings}}\n"
            + "\n"
            + "<small>Nothing to check out...</small>\n"
            + "{{/hasThings}}";

    shouldCompileTo(
        string,
        new Things(
            List.of(
                new Thing("@fat", "one"), new Thing("@dhg", "two"), new Thing("@sayrer", "three")),
            true),
        "<strong>This is a slightly more complicated blah.</strong>.\n"
            + "Check this out:\n"
            + "<ul>\n"
            + "<li class=one>@fat</li>\n"
            + "<li class=two>@dhg</li>\n"
            + "<li class=three>@sayrer</li>\n"
            + "</ul>.\n");
  }

  @Test
  @DisplayName("Nested if else rendering")
  public void Nestedifelserendering() throws IOException {
    Hash helpers =
        $(
            "blk",
            (Helper<Object>) (context, options) -> options.fn(),
            "inverse",
            (Helper<Object>) (context, options) -> options.inverse());
    shouldCompileTo(
        "{{#inverse}} {{#blk}} Unexpected {{/blk}} {{else}}  {{#blk}} Expected {{/blk}}"
            + " {{/inverse}}",
        $,
        helpers,
        "   Expected  ");

    shouldCompileTo(
        "{{#inverse}} {{#blk}} Unexpected {{/blk}} {{else}} {{#blk}} Expected {{/blk}}"
            + " {{/inverse}}",
        $,
        helpers,
        "  Expected  ");
  }

  @Test
  @DisplayName("Scoped this identifier")
  public void Scopedthisidentifier() throws IOException {
    shouldCompileTo("{{./foo}}", $("foo", "bar"), "bar");
  }

  @Test
  @DisplayName("Unicode line terminators")
  public void Unicodelineterminators() throws IOException {
    shouldCompileTo("\u2028", $, "\u2028");
  }

  @Test
  @DisplayName("Using array in escaping mustache fails")
  public void Usingarrayinescapingmustachefails() throws IOException {
    shouldCompileTo("{{arr}}", $("arr", List.of(1, 2)), "[1, 2]");
  }

  @Test
  @DisplayName("Using array in escaping mustache fails (primitive array)")
  public void Usingarrayinescapingmustachefails_primitive() throws IOException {
    shouldCompileTo("{{arr}}", $("arr", new int[] {1, 2}), "[1, 2]");
  }

  @Test
  @DisplayName("Mustache man page")
  public void mustachemanpage() throws IOException {
    shouldCompileTo(
        "Hello {{name}}. You have just won ${{value}}!{{#in_ca}} Well, ${{taxed_value}}, after"
            + " taxes.{{/in_ca}}",
        $("name", "Chris", "value", 10000, "taxed_value", 10000 - 10000 * 0.4, "in_ca", true),
        "Hello Chris. You have just won $10000! Well, $6000.0, after taxes.");
  }

  @Test
  @DisplayName("zero context rendering")
  public void zerocontextrendering() throws IOException {
    shouldCompileTo(
        "{{#foo}} This is {{bar}} ~ {{/foo}}", $("foo", 0, "bar", "OK"), " This is  ~ ");

    shouldCompileTo("{{#foo}}This is {{bar}} ~ {{/foo}}", $("foo", 1, "bar", "OK"), "This is  ~ ");
  }

  @Test
  @DisplayName("zero pathed rendering")
  public void zeropathedrendering() throws IOException {
    shouldCompileTo("{{foo.bar}}", $("foo", 0), "");
  }

  @Test
  @DisplayName("undefined values for helpers")
  public void undefinedvaluesforhelpers() throws IOException {
    shouldCompileTo(
        "{{str bar.baz}}",
        $,
        $("str", (Helper<Object>) (context, options) -> "" + context),
        "null");
  }

  @Test
  @DisplayName("Depths and de-dupe")
  public void Depthsanddedupe() throws IOException {
    shouldCompileTo(
        "{{#if dater}}{{#each data}}{{../name}}{{/each}}{{else}}{{#each"
            + " notData}}{{../name}}{{/each}}{{/if}}",
        $("name", "foo", "data", List.of(1), "notData", List.of(1)),
        "foo");
  }

  @Test
  @DisplayName("Each empty string key")
  public void Eachemptystringkey() throws IOException {
    shouldCompileTo(
        "{{#each data}}Key: {{@key}}\n{{/each}}",
        $("data", $("", "foo", "name", "Chris", "value", 10000)),
        "Key: \nKey: name\nKey: value\n");
  }

  @Test
  @DisplayName("Should handle simple safe string responses")
  public void Shouldhandlesimplesafestringresponses() throws IOException {
    shouldCompileTo(
        "{{#wrap}}{{>partial}}{{/wrap}}",
        $,
        $("wrap", (Helper<Object>) (context, options) -> new Handlebars.SafeString(options.fn())),
        $("partial", "{{#wrap}}<partial>{{/wrap}}"),
        "<partial>",
        "");
  }

  @Test
  @DisplayName("Sparse arrays")
  public void Sparsearrays() throws IOException {
    // index is always 0-int-base
    shouldCompileTo(
        "{{#each array}}{{@index}}{{.}}{{/each}}",
        $("array", $("1", "foo", "3", "bar")),
        "0foo1bar");
    // use key for maps
    shouldCompileTo(
        "{{#each array}}{{@key}}{{.}}{{/each}}", $("array", $("1", "foo", "3", "bar")), "1foo3bar");
  }

  @Test
  @DisplayName("should support multiple levels of inline partials")
  public void shouldsupportmultiplelevelsofinlinepartials() throws IOException {
    shouldCompileToWithPartials(
        "{{#> layout}}{{#*inline \"subcontent\"}}subcontent{{/inline}}{{/layout}}",
        $,
        $(
            "doctype",
            "doctype{{> content}}",
            "layout",
            "{{#> doctype}}{{#*inline \"content\"}}layout{{> subcontent}}{{/inline}}{{/doctype}}"),
        "doctypelayoutsubcontent");
  }

  @Test
  @DisplayName("should support failover content in multiple levels of inline partials")
  public void shouldsupportfailovercontentinmultiplelevelsofinlinepartials() throws IOException {
    shouldCompileToWithPartials(
        "{{#> layout}}{{/layout}}",
        $,
        $(
            "doctype",
            "doctype{{> content}}",
            "layout",
            "{{#> doctype}}{{#*inline \"content\"}}layout{{#>"
                + " subcontent}}subcontent{{/subcontent}}{{/inline}}{{/doctype}}"),
        "doctypelayoutsubcontent");
  }

  @Test
  @DisplayName("should support greater than 3 nested levels of inline partials")
  public void shouldsupportgreaterthan3nestedlevelsofinlinepartials() throws IOException {
    shouldCompileToWithPartials(
        "{{#> layout}}Outer{{/layout}}",
        $,
        $("inner", "", "layout", "{{#> inner}}Inner{{/inner}}{{> @partial-block }}"),
        "Outer");
  }

  @Test
  @DisplayName("Context handling within each iteration")
  public void Contexthandlingwithineachiteration() throws IOException {
    prettyPrint = true;
    shouldCompileTo(
        "{{#each array}}\n"
            + " 1. IF: {{#if true}}{{../name}}-{{../../name}}-{{../../../name}}{{/if}}\n"
            + " 2. MYIF: {{#myif true}}{{../name}}={{../../name}}={{../../../name}}{{/myif}}\n"
            + "{{/each}}",
        $("array", List.of(1), "name", "John"),
        $(
            "myif",
            (Helper<Object>)
                (context, options) -> {
                  return Boolean.TRUE.equals(context) ? options.fn() : options.inverse();
                }),
        " 1. IF: John--\n 2. MYIF: John==\n");
  }

  @Test
  @DisplayName("Support block params for existing programs")
  public void Supportblockparamsforexistingprograms() throws IOException {
    shouldCompileTo(
        "{{#*inline \"test\"}}{{> @partial-block }}{{/inline}}"
            + "{{#>test }}{{#each listOne as |item|}}{{ item }}{{/each}}{{/test}}"
            + "{{#>test }}{{#each listTwo as |item|}}{{ item }}{{/each}}{{/test}}",
        $("listOne", List.of("a"), "listTwo", List.of("b")),
        "ab");
  }

  @Test
  @DisplayName("should allow hash with protected array names")
  public void shouldallowhashwithprotectedarraynames() throws IOException {
    shouldCompileTo(
        "{{helpa length=\"foo\"}}",
        $("array", List.of(1), "name", "John"),
        $("helpa", (Helper<Object>) (context, options) -> options.hash("length")),
        "foo");
  }

  @Test
  @DisplayName("\"unless\" breaks when \"each\" value equals \"null\"")
  public void unlessbreakswheneachvalueequalsnull() throws IOException {
    shouldCompileTo(
        "{{#each list}}{{#unless ./prop}}parent={{../value}} {{/unless}}{{/each}}",
        $("value", "parent", "list", Arrays.asList(null, "a")),
        "parent=parent parent=parent ");
  }

  @Test
  @DisplayName("breaks {{#if @partial-block}} usage")
  public void breakspartialblockusage() throws IOException {
    shouldCompileToWithPartials(
        "template {{>partial}} template",
        $,
        $(
            "partialWithBlock",
            "{{#if @partial-block}} block {{> @partial-block}} block {{/if}}",
            "partial",
            "{{#> partialWithBlock}} partial {{/partialWithBlock}}"),
        "template  block  partial  block  template");
  }

  @Test
  public void probewearedoingitwrong() throws IOException {
    prettyPrint = true;
    String template =
        "{{#each array}}\n"
            + "  {{#mywith user}}\n"
            + "  Item: {{../name}}\n"
            + "  Root: {{../../name}}\n"
            + "  {{/mywith}}\n"
            + "{{/each}}\n";
    Helper<Object> mywithThis = (context, options) -> options.fn(options.context);
    Helper<Object> mywith = (context, options) -> options.fn(context);
    Object input =
        $(
            "name",
            "Global_Admin",
            "array",
            List.of($("name", "Local_John", "user", $("role", "Manager"))));

    shouldCompileTo(template, input, $("mywith", mywithThis), "  Item: Global_Admin\n  Root: \n");

    // setting context to first argument of helper
    shouldCompileTo(
        template, input, $("mywith", mywith), "  Item: Local_John\n  Root: Global_Admin\n");
  }
}
