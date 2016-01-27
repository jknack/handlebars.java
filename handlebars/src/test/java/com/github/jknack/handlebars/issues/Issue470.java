package com.github.jknack.handlebars.issues;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue470 extends v4Test {

  @Test
  public void indexFromParentContext() throws IOException {
    shouldCompileTo("{{#each families}} {{#each this}}{{../@index}} :{{name}} {{/each}} {{/each}}",
        $("hash", $("families", Arrays.asList(
            Arrays.asList(
                $("age", 10, "name", "jimmy"),
                $("age", 15, "name", "rose")),
            Arrays.asList(
                $("age", 35, "name", "John"),
                $("age", 32, "name", "Jessy"))))),
        " 0 :jimmy 0 :rose   1 :John 1 :Jessy  ");
  }

  @Test
  public void indexFromParentContextInJs() throws IOException {
    shouldCompileTo("{{#each families}} {{#each this}}{{@../index}} :{{name}} {{/each}} {{/each}}",
        $("hash", $("families", Arrays.asList(
            Arrays.asList(
                $("age", 10, "name", "jimmy"),
                $("age", 15, "name", "rose")),
            Arrays.asList(
                $("age", 35, "name", "John"),
                $("age", 32, "name", "Jessy"))))),
        " 0 :jimmy 0 :rose   1 :John 1 :Jessy  ");
  }

}
