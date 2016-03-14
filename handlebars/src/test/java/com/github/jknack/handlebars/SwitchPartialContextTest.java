package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class SwitchPartialContextTest extends AbstractTest {

  @Test
  public void switchPartialContext() throws IOException {
    Hash hash = $("name", "root", "context", $("name", "context", "child", $("name", "child")));
    Hash partials = $("partial", "{{name}}");
//    shouldCompileToWithPartials("{{>partial}}", hash, partials, "root");
//    shouldCompileToWithPartials("{{>partial this}}", hash, partials, "root");
    shouldCompileToWithPartials("{{>partial context}}", hash, partials, "context");
    shouldCompileToWithPartials("{{>partial context.name}}", hash, partials, "root");
    shouldCompileToWithPartials("{{>partial context.child}}", hash, partials, "child");
  }

  @Test
  public void partialWithContext() throws IOException {
    String partial = "{{#this}}{{name}} {{/this}}";
    Hash hash = $("dudes", new Object[]{$("name", "moe"), $("name", "curly") });

    shouldCompileToWithPartials("Dudes: {{>dude dudes}}", hash, $("dude", partial),
        "Dudes: moe curly ");
  }
}
