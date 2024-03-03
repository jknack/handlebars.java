/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue489 extends v4Test {

  @Test
  public void shouldPassObjectResult() throws IOException {
    shouldCompileTo(
        "{{#each myLibrary}}"
            + "{{#with (lookup @root.booksByIsbn .) as |book|}}"
            + "{{book.title}} by {{book.author}} ({{book.isbn}})\n"
            + "{{/with}}"
            + "{{/each}}",
        $(
            "hash",
            $(
                "booksByIsbn",
                $(
                    "0321356683",
                    $(
                        "title",
                        "Effective Java (2nd Edition)",
                        "author",
                        "Joshua Bloch",
                        "isbn",
                        "0321356683")),
                "myLibrary",
                new String[] {"0321356683"})),
        "Effective Java (2nd Edition) by Joshua Bloch (0321356683)\n");
  }
}
