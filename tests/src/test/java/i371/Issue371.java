/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package i371;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.google.common.collect.ImmutableList;

public class Issue371 extends AbstractTest {

  @Test
  public void immlistShouldHavePseudoVars() throws IOException {
    shouldCompileTo(
        "<ul class=\"fleft clear_fix\">\n"
            + "            {{#each menuLinks}}\n"
            + "                <li {{#if @first}}class=\"first\"{{/if}}><a"
            + " href=\"{{href}}\">{{title}}</a></li>\n"
            + "            {{/each}}\n"
            + "        </ul>",
        $(
            "menuLinks",
            ImmutableList.of(
                $("href", "h1.org", "title", "t1"), $("href", "h2.org", "title", "t2"))),
        "<ul class=\"fleft clear_fix\">\n"
            + "            \n"
            + "                <li class=\"first\"><a href=\"h1.org\">t1</a></li>\n"
            + "            \n"
            + "                <li ><a href=\"h2.org\">t2</a></li>\n"
            + "            \n"
            + "        </ul>");
  }
}
