package com.github.jknack.handlebars.i204;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class Issue204 extends AbstractTest {

  @Test
  public void ifElseBlockMustBeIncludedInRawText() throws IOException {
    assertEquals("{{#if true}}true{{else}}false{{/if}}",
        compile("{{#if true}}true{{else}}false{{/if}}").text());
    assertEquals("{{#if true}}true{{^}}false{{/if}}", compile("{{#if true}}true{{^}}false{{/if}}")
        .text());
  }

  @Test
  public void ifElseBlockMustBeIncludedInJavaScript() throws IOException {
    String jsFn = "function (Handlebars,depth0,helpers,partials,data) {\n"
        +
        "  this.compilerInfo = [4,'>= 1.0.0'];\n"
        +
        "helpers = this.merge(helpers, Handlebars.helpers); data = data || {};\n"
        +
        "  var stack1, self=this;\n"
        +
        "\n"
        +
        "function program1(depth0,data) {\n"
        +
        "  \n"
        +
        "  \n"
        +
        "  return \"true\";\n"
        +
        "  }\n"
        +
        "\n"
        +
        "function program3(depth0,data) {\n"
        +
        "  \n"
        +
        "  \n"
        +
        "  return \"false\";\n"
        +
        "  }\n"
        +
        "\n"
        +
        "  stack1 = helpers['if'].call(depth0, true, {hash:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),data:data});\n"
        +
        "  if(stack1 || stack1 === 0) { return stack1; }\n" +
        "  else { return ''; }\n" +
        "  }";
    assertEquals(jsFn, new Handlebars().handlebarsJsFile("/handlebars-v1.3.0.js").compileInline("{{#if true}}true{{else}}false{{/if}}").toJavaScript());
    assertEquals(jsFn, new Handlebars().handlebarsJsFile("/handlebars-v1.3.0.js").compileInline("{{#if true}}true{{^}}false{{/if}}").toJavaScript());
  }

}
