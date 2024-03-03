/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Demostrate error reporting. It isn't a test.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
public class HelperErrorTest extends AbstractTest {

  Hash source =
      $(
          "helper",
          "\n{{#block}} {{/block}}",
          "embedded",
          "\n{{#embedded}} {{/embedded}}",
          "basic",
          "\n{{basic}}",
          "notfoundblock",
          "\n{{#notfound hash=x}}{{/notfound}}",
          "notfound",
          "\n{{notfound hash=x}}");

  @Test
  public void block() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("helper"));
  }

  @Test
  public void notfound() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("notfound"));
  }

  @Test
  public void notfoundblock() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("notfoundblock"));
  }

  @Test
  public void basic() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("basic"));
  }

  @Test
  public void embedded() throws IOException {
    assertThrows(HandlebarsException.class, () -> parse("embedded"));
  }

  private Object parse(final String uri) throws IOException {
    try {
      Hash helpers =
          $(
              "basic",
              new Helper<Object>() {
                @Override
                public Object apply(final Object context, final Options options)
                    throws IOException {
                  throw new IllegalArgumentException("missing parameter: '0'.");
                }
              });
      shouldCompileTo((String) source.get(uri), $, helpers, "must fail");
      throw new IllegalStateException("An error is expected");
    } catch (HandlebarsException ex) {
      Handlebars.log(ex.getMessage());
      throw ex;
    }
  }
}
