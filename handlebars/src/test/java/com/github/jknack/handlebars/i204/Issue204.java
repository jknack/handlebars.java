package com.github.jknack.handlebars.i204;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue204 extends AbstractTest {

  @Test
  public void ifElseBlockMustBeIncludedInRawText() throws IOException {
    assertEquals("{{#if true}}true{{else}}false{{/if}}",
        compile("{{#if true}}true{{else}}false{{/if}}").text());
    assertEquals("{{#if true}}true{{^}}false{{/if}}", compile("{{#if true}}true{{^}}false{{/if}}")
        .text());
  }

}
