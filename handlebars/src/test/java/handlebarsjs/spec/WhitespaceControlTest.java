/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/whitespace-control.js */
@DisplayName("whitespace-control")
public class WhitespaceControlTest extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.setPrettyPrint(true);
  }

  @Test
  @DisplayName("should strip whitespace around mustache calls")
  public void shouldstripwhitespacearoundmustachecalls() throws IOException {
    Hash hash = $("foo", "bar<");

    shouldCompileTo(" {{~foo~}} ", hash, "bar&lt;");

    shouldCompileTo(" {{~foo}} ", hash, "bar&lt; ");

    shouldCompileTo(" {{foo~}} ", hash, " bar&lt;");

    shouldCompileTo(" {{~&foo~}} ", hash, "bar<");

    shouldCompileTo(" {{~{foo}~}} ", hash, "bar<");

    shouldCompileTo("1\n{{foo~}} \n\n 23\n{{bar}}4", $(), "1\n23\n4");
  }

  @Test
  @DisplayName("should strip whitespace around simple block calls")
  public void shouldstripwhitespacearoundsimpleblockcalls() throws IOException {
    Hash hash = $("foo", "bar<");

    shouldCompileTo(" {{~#if foo~}} bar {{~/if~}} ", hash, "bar");

    shouldCompileTo(" {{#if foo~}} bar {{/if~}} ", hash, " bar ");

    shouldCompileTo(" {{~#if foo}} bar {{~/if}} ", hash, " bar ");

    shouldCompileTo(" {{#if foo}} bar {{/if}} ", hash, "  bar  ");

    shouldCompileTo(" \n\n{{~#if foo~}} \n\nbar \n\n{{~/if~}}\n\n ", hash, "bar");

    shouldCompileTo(" a\n\n{{~#if foo~}} \n\nbar \n\n{{~/if~}}\n\na ", hash, " abara ");
  }

  @Test
  @DisplayName("should strip whitespace around inverse block calls")
  public void shouldstripwhitespacearoundinverseblockcalls() throws IOException {
    shouldCompileTo(" {{~^if foo~}} bar {{~/if~}} ", $(), "bar");

    shouldCompileTo(" {{^if foo~}} bar {{/if~}} ", $(), " bar ");

    shouldCompileTo(" {{~^if foo}} bar {{~/if}} ", $(), " bar ");

    shouldCompileTo(" {{^if foo}} bar {{/if}} ", $(), "  bar  ");

    shouldCompileTo(" \n\n{{~^if foo~}} \n\nbar \n\n{{~/if~}}\n\n ", $(), "bar");
  }

  @Test
  @DisplayName("should strip whitespace around complex block calls")
  public void shouldstripwhitespacearoundcomplexblockcalls() throws IOException {
    Hash hash = $("foo", "bar<");

    shouldCompileTo("{{#if foo~}} bar {{~^~}} baz {{~/if}}", hash, "bar");

    shouldCompileTo("{{#if foo~}} bar {{^~}} baz {{/if}}", hash, "bar ");

    shouldCompileTo("{{#if foo}} bar {{~^~}} baz {{~/if}}", hash, " bar");

    shouldCompileTo("{{#if foo}} bar {{^~}} baz {{/if}}", hash, " bar ");

    shouldCompileTo("{{#if foo~}} bar {{~else~}} baz {{~/if}}", hash, "bar");

    shouldCompileTo("\n\n{{~#if foo~}} \n\nbar \n\n{{~^~}} \n\nbaz \n\n{{~/if~}}\n\n", hash, "bar");

    shouldCompileTo(
        "\n\n{{~#if foo~}} \n\n{{{foo}}} \n\n{{~^~}} \n\nbaz \n\n{{~/if~}}\n\n", hash, "bar<");

    shouldCompileTo("{{#if foo~}} bar {{~^~}} baz {{~/if}}", $(), "baz");

    shouldCompileTo("{{#if foo}} bar {{~^~}} baz {{/if}}", $(), "baz ");

    shouldCompileTo("{{#if foo~}} bar {{~^}} baz {{~/if}}", $(), " baz");

    shouldCompileTo("{{#if foo~}} bar {{~^}} baz {{/if}}", $(), " baz ");

    shouldCompileTo("{{#if foo~}} bar {{~else~}} baz {{~/if}}", $(), "baz");

    shouldCompileTo("\n\n{{~#if foo~}} \n\nbar \n\n{{~^~}} \n\nbaz \n\n{{~/if~}}\n\n", $(), "baz");
  }

  @Test
  @DisplayName("should strip whitespace around partials")
  public void shouldstripwhitespacearoundpartials() throws IOException {
    shouldCompileToWithPartials("foo {{~> dude~}} ", $(), $("dude", "bar"), "foobar");

    shouldCompileToWithPartials("foo {{> dude~}} ", $(), $("dude", "bar"), "foo bar");

    shouldCompileToWithPartials("foo {{> dude}} ", $(), $("dude", "bar"), "foo bar ");

    shouldCompileToWithPartials("foo\n {{~> dude}} ", $(), $("dude", "bar"), "foobar");

    shouldCompileToWithPartials("foo\n {{> dude}} ", $(), $("dude", "bar"), "foo\n bar");
  }

  @Test
  @DisplayName("should only strip whitespace once")
  public void shouldonlystripwhitespaceonce() throws IOException {
    shouldCompileTo(" {{~foo~}} {{foo}} {{foo}} ", $("foo", "bar"), "barbar bar ");
  }
}
