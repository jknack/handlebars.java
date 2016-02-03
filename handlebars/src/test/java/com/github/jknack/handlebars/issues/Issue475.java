package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue475 extends v4Test {

  @Test
  public void shouldNotLostPartialParamBetweenInvocations() throws IOException {
    shouldCompileTo("{{> p name=\"firstName\" suffix=suffix}}\n"
        + "{{> p name=\"lastName\" suffix=suffix}}\n"
        + "{{> p name=\"streetName\" suffix=suffix}}",
        $("hash", $("suffix", "foo"), "partials", $("p", "{{name}} {{suffix}}")),
        "firstName foo\n" +
            "lastName foo\n" +
            "streetName foo");
  }

  @Test
  public void shouldNotLostPartialParamBetweenInvocationsWithoutRedundantParam()
      throws IOException {
    final String template = "{{> q name=\"firstName\" suffix=suffix}}\n" +
        "{{> q name=\"lastName\" suffix=suffix}}\n" +
        "{{> q name=\"streetName\" suffix=suffix}}";
    shouldCompileTo("{{> p suffix=\"foo\"}}", // fails
        $("partials", $("p", template, "q", "{{name}} {{suffix}}")),
          "firstName foo\n" +
          "lastName foo\n" +
          "streetName foo");
  }

}
