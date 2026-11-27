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

public class Issue727 extends AbstractTest {

  @Test
  public void shouldResolveTheRightIndex() throws IOException {
    shouldCompileTo(
        "<ul>\n"
            + "    {{#each materials}} <!-- the list of Materials-->\n"
            + "        <li>{{@index}}</li>\n"
            + "    {{/each}}"
            + "</ul>",
        $("materials", List.of($("index", "3"), $("index", "3"))),
        "<ul>\n"
            + "     <!-- the list of Materials-->\n"
            + "        <li>0</li>\n"
            + "     <!-- the list of Materials-->\n"
            + "        <li>1</li>\n"
            + "    </ul>");
  }
}
