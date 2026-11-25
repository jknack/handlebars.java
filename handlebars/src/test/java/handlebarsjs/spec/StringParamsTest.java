/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Helper;

/** https://github.com/handlebars-lang/handlebars.js/blob/master/spec/string-params.js */
@DisplayName("string params mode")
public class StringParamsTest extends AbstractTest {

  private static final String NO_CONTEXTS =
      "no equivalent API: options.contexts and options.hashContexts are not available";

  private void shouldCompileToWithStringParams(
      final String template, final Object data, final Hash helpers, final String expected)
      throws IOException {
    assertEquals(expected, compile(template, helpers, true).apply(data));
  }

  @Test
  @DisplayName("arguments to helpers can be retrieved from options hash in string form")
  public void argumentstohelperscanberetrievedfromoptionshashinstringform() throws IOException {
    shouldCompileToWithStringParams(
        "{{wycats is.a slave.driver}}",
        $(),
        $(
            "wycats",
            (Helper<Object>)
                (passiveVoice, options) ->
                    "HELP ME MY BOSS " + passiveVoice + " " + options.param(0)),
        "HELP ME MY BOSS is.a slave.driver");
  }

  @Test
  @DisplayName(
      "when using block form, arguments to helpers can be retrieved from options hash in string"
          + " form")
  public void whenusingblockformargumentstohelperscanberetrievedfromoptionshashinstringform()
      throws IOException {
    shouldCompileToWithStringParams(
        "{{#wycats is.a slave.driver}}help :({{/wycats}}",
        $(),
        $(
            "wycats",
            (Helper<Object>)
                (passiveVoice, options) ->
                    "HELP ME MY BOSS "
                        + passiveVoice
                        + " "
                        + options.param(0)
                        + ": "
                        + options.fn(options.context)),
        "HELP ME MY BOSS is.a slave.driver: help :(");
  }

  @Test
  @DisplayName("with nested block ambiguous")
  public void withnestedblockambiguous() throws IOException {
    shouldCompileToWithStringParams(
        "{{#with content}}{{#view}}{{firstName}} {{lastName}}{{/view}}{{/with}}",
        $(),
        $(
            "with",
            (Helper<Object>) (context, options) -> "WITH",
            "view",
            (Helper<Object>) (context, options) -> "VIEW"),
        "WITH");
  }

  @Test
  @Disabled(NO_CONTEXTS)
  @DisplayName(
      "when inside a block in String mode, .. passes the appropriate context in the options hash")
  public void wheninsideablockinstringmodedotdotpassestheappropriatecontextintheoptionshash()
      throws IOException {}

  @Test
  @DisplayName("information about the types is passed along")
  public void informationaboutthetypesispassedalong() throws IOException {
    // options.types is not available: only the string and raw forms are verified
    shouldCompileToWithStringParams(
        "{{tomdale 'need' dad.joke true false}}",
        $(),
        $(
            "tomdale",
            (Helper<Object>)
                (desire, options) -> {
                  assertEquals("need", desire);
                  assertEquals("dad.joke", options.param(0));
                  assertEquals(true, options.param(1));
                  assertEquals(false, options.param(2));
                  return "Helper called";
                }),
        "Helper called");
  }

  @Test
  @DisplayName("hash parameters get type information")
  public void hashparametersgettypeinformation() throws IOException {
    // options.types and options.hashTypes are not available: only the values are verified
    shouldCompileToWithStringParams(
        "{{tomdale he.says desire='need' noun=dad.joke bool=true}}",
        $(),
        $(
            "tomdale",
            (Helper<Object>)
                (exclamation, options) -> {
                  assertEquals("he.says", exclamation);
                  assertEquals("need", options.hash("desire"));
                  assertEquals("dad.joke", options.hash("noun"));
                  assertEquals(true, options.hash("bool"));
                  return "Helper called";
                }),
        "Helper called");
  }

  @Test
  @Disabled(NO_CONTEXTS)
  @DisplayName("hash parameters get context information")
  public void hashparametersgetcontextinformation() throws IOException {}

  @Test
  @Disabled(NO_CONTEXTS)
  @DisplayName(
      "when inside a block in String mode, .. passes the appropriate context in the options hash"
          + " to a block helper")
  public void
      wheninsideablockinstringmodedotdotpassestheappropriatecontextintheoptionshashtoablockhelper()
          throws IOException {}

  @Test
  @DisplayName("should handle DATA")
  public void shouldhandledata() throws IOException {
    shouldCompileToWithStringParams(
        "{{foo @bar}}",
        $(),
        $(
            "foo",
            (Helper<Object>)
                (bar, options) -> {
                  assertEquals("@bar", bar);
                  return "Foo!";
                }),
        "Foo!");
  }
}
