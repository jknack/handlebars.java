package com.github.jknack.handlebars.i334;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue334 extends AbstractTest {

  @Test
  public void withHelperSpec() throws IOException {
    shouldCompileTo("{{#each this}}{{@first}}{{/each}}", $("one", 1, "two", 2), "first");
  }
}
