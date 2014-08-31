package com.github.jknack.handlebars.i288;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue288 extends AbstractTest {

  @Test
  public void i288() throws Exception {
    shouldCompileTo("{{#each array1}}" +
        "index_before - {{@index}}\n" +
        "{{#each array2}}" +
        "{{/each}}" +
        "index_after - {{@index}}\n" +
        "{{/each}}",
        $("array1", new Object[]{$("array2", new Object[]{0, 1, 2 }) }),
        "index_before - 0\n" +
        "index_after - 0\n");
  }
}
