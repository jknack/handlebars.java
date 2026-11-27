/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue729 extends AbstractTest {

  @Override
  protected void configure(final com.github.jknack.handlebars.Handlebars handlebars) {
    handlebars.setPrettyPrint(true);
  }

  @Test
  public void incorrectScopeOnLast() throws IOException {
    shouldCompileTo(
        "<ul>\n"
            + " {{#each data as |value key|}}\n"
            + "    {{#each @value}}{{{fieldName}}} {{#unless @last}}&& {{/unless}}{{/each}}\n"
            + "    <br/>\n"
            + " {{/each}}\n"
            + "</ul>",
        $(
            "data",
            $(
                "identity1",
                List.of(
                    $("fieldType", "ID", "fieldName", "id1"),
                    $("fieldType", "Passport", "fieldName", "passport1")),
                "identity2",
                List.of(
                    $("fieldType", "ID", "fieldName", "id2"),
                    $("fieldType", "Passport", "fieldName", "passport2")))),
        "<ul>\n"
            + "    id1 && passport1 \n"
            + "    <br/>\n"
            + "    id2 && passport2 \n"
            + "    <br/>\n"
            + "</ul>");
  }
}
