package com.github.jknack.handlebars.issues;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue467 extends v4Test {

  @Test
  public void shouldHaveAccessToKeyViaBlockParam() throws IOException {
    shouldCompileTo("{{#each this as |eitem key| }}{{this}} || {{key}} | {{eitem}}{{/each}}",
        $("hash", $("africa", Arrays.asList("egypt", "kenya"))),
        "[egypt, kenya] || africa | [egypt, kenya]");
  }

}
