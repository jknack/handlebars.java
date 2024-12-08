/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Demostrate error reporting. It isn't a test.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
public class ParsingErrorTest extends AbstractTest {

  Hash source =
      $(
          "inbox/inbox",
          "{{value",
          "block",
          "{{#block}}{{/nan}}",
          "iblock",
          "{{#block}}invalid block",
          "delim",
          "{{=<% %>=}} <%Hello",
          "default",
          "{{> missingPartial}}",
          "partial",
          "{{#value}}",
          "invalidChar",
          "\n{{tag message.from \\\"user\\\"}}\n",
          "root",
          "{{> p1}}",
          "p1",
          "{{value",
          "deep",
          "{{> deep1}}",
          "deep1",
          " {{> deep2",
          "unbalancedDelim",
          "{{=<%%>=}}",
          "partialName",
          "{{> /user}}",
          "partialName2",
          "{{> /layout/base}}",
          "paramOrder",
          "{{f param hashx=1 param}}",
          "idx1",
          "{{list[0]}}",
          "idx2",
          "{{list.[0}}",
          "idx3",
          "{{list.[]}}",
          "idx4",
          "{{list.[}}",
          "multipleElse",
          "{{#if true}} b1 {{else}} b2 {{else}} b3 {{/if}}");

  @Test
  public void correctPath() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("inbox/inbox"));
  }

  @Test
  public void missingPartial() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("default"));
  }

  @Test
  public void invalidChar() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("invalidChar"));
  }

  @Test
  public void level1() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("root"));
  }

  @Test
  public void level2() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("deep"));
  }

  @Test
  public void block() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("block"));
  }

  @Test
  public void unbalancedDelim() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("unbalancedDelim"));
  }

  @Test
  public void delim() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("delim"));
  }

  @Test
  public void paramOutOfOrder() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("paramOrder"));
  }

  @Test
  public void iblock() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("iblock"));
  }

  @Test
  public void tvar() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("{{{tvar"));
  }

  @Test
  public void tvarDelim() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("{{=** **=}}**{tvar"));
  }

  @Test
  public void ampvar() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("{{&tvar"));
  }

  @Test
  public void ampvarDelim() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("{{=** **=}}**&tvar"));
  }

  @Test
  public void missingId() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("{{is"));
  }

  @Test
  public void partialName() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("partialName"));
  }

  @Test
  public void partialName2() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("partialName2"));
  }

  @Test
  public void idx1() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("idx1"));
  }

  @Test
  public void idx2() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("idx2"));
  }

  @Test
  public void idx3() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("idx3"));
  }

  @Test
  public void idx4() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("idx4"));
  }

  @Test
  public void multipleElse() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("multipleElse"));
  }

  private void parse(final String candidate) throws IOException {
    try {
      String input = (String) source.get(candidate);
      Template compiled = compile(input == null ? candidate : input, $(), source);
      compiled.apply(new Object());

      System.out.println(compiled);
      fail("An error is expected");
    } catch (HandlebarsException ex) {
      Handlebars.log(ex.getMessage());
      throw ex;
    }
  }
}
