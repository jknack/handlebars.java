package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class Issue329 extends AbstractTest {

  @Test
  public void traversal() throws IOException {
    String template = "{{#each hbstest.objects}}\n" +
        "    {{#each ../hbstest.objects}}\n" +
        "          {{../../hbstest.prop}}   {{!--works in hbs java and handlebars.js--}}\n" +
        "    {{/each}}\n" +
        "{{/each}}";
    shouldCompileTo(
        template,
        $("hbstest",
            $("prop", "foo", "objects", new Object[]{$("prop", "inner-foo", "prop2", "bar") })),
            "\n    \n" +
            "          foo   \n" +
            "    \n");
  }

  @Test
  public void traversalWithIf() throws IOException {
    String template = "{{#each hbstest.objects}}\n"
        +
        "    {{../hbstest.prop}} {{!--works in hbs java and handlebars.js--}}\n"
        +
        "    {{#if ../hbstest.objects}} {{!-- conditional fires in both hbs java and handlebars.js--}}\n"
        +
        "        {{../../hbstest.prop}} {{!--only resolves in handlebars.js--}}\n" +
        "    {{/if}}\n" +
        "{{/each}}";
    shouldCompileTo(
        template,
        $("hbstest",
            $("prop", "foo", "objects", new Object[]{$("prop", "inner-foo", "prop2", "bar") })),
        "\n    foo \n" +
            "     \n" +
            "        foo \n" +
            "    \n");
  }

}
