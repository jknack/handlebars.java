package com.github.jknack.handlebars.i401;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class Issue401 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.prettyPrint(true);
    try {
      handlebars.registerHelpers(new File("src/test/resources/issue401.js"));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  public void shouldIndentPartial() throws IOException {
    shouldCompileToWithPartials("{{#each items}}\n" +
        "parent line 1 {{this}}\n" +
        "    {{#withKey ../map key=this}}\n" +
        "        {{#each this}}\n" +
        "    {{> child}}\n" +
        "        {{/each}}\n" +
        "    {{/withKey}}\n" +
        "parent line 2 {{this}}\n" +
        "{{/each}}", $("items", Arrays.asList("a", "b", "c"),
            "map", $(
                "a", Arrays.asList("one", "two", "three"),
                "b", Arrays.asList("four", "five", "six"),
                "c", Arrays.asList("seven", "eight", "nine"))),
        $("child", "child {{this}}\n"), "parent line 1 a\n" +
            "    child one\n" +
            "    child two\n" +
            "    child three\n" +
            "parent line 2 a\n" +
            "parent line 1 b\n" +
            "    child four\n" +
            "    child five\n" +
            "    child six\n" +
            "parent line 2 b\n" +
            "parent line 1 c\n" +
            "    child seven\n" +
            "    child eight\n" +
            "    child nine\n" +
            "parent line 2 c\n" +
            "");
  }
}
