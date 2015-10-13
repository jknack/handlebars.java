package com.github.jknack.handlebars.i418;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue418 extends AbstractTest {

  @Test
  public void lookupHelper() throws IOException {
    shouldCompileTo("{{#each series}} {{lookup ../types this}}{{/each}}",
        $("series", new String[]{"Test A", "Test B", "Test C" }, "types",
            $("Test A", "bar", "Test B", "bar", "Test C", "line")),
        " bar bar line");
  }
}
