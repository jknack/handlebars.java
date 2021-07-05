package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

import java.io.IOException;

public class Issue698 extends v4Test {

  @Test
  public void shouldUseContext() throws IOException {
    shouldCompileTo("{{#each value}}" +
            "{{@index}}={{#if this}}true{{else}}false{{/if}}\n" +
            "zero{{@index}}={{#if this includeZero=true}}true{{else}}false{{/if}}\n" +
            "{{/each}}",
        $("hash", $("value", new Object[]{null, 0, 0.0, 1, "", "0"})),
        "0=false\n" +
            "zero0=false\n" +
            "1=false\n" +
            "zero1=true\n" +
            "2=false\n" +
            "zero2=true\n" +
            "3=true\n" +
            "zero3=true\n" +
            "4=false\n" +
            "zero4=false\n" +
            "5=true\n" +
            "zero5=true\n");
  }

}
