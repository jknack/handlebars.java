/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i350;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class Issue350 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.prettyPrint(true);
  }

  @Test
  public void partialWithParameters() throws IOException {
    shouldCompileToWithPartials(
        "<ul>\n"
            + "{{#each dudes}}\n"
            + "  {{> dude title=../title class=\"list-item\"}}\n"
            + "{{/each}}\n"
            + "</ul>",
        $(
            "title",
            "profile",
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            }),
        $(
            "dude",
            "<li class=\"{{class}}\">\n"
                + "  {{title}}: <a href=\"{{url}}\">{{name}}</a>\n"
                + "</li>\n"),
        "<ul>\n"
            + "  <li class=\"list-item\">\n"
            + "    profile: <a href=\"http://yehuda\">Yehuda</a>\n"
            + "  </li>\n"
            + "  <li class=\"list-item\">\n"
            + "    profile: <a href=\"http://alan\">Alan</a>\n"
            + "  </li>\n"
            + "</ul>");
  }
}
