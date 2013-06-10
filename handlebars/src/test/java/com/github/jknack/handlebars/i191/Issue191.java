package com.github.jknack.handlebars.i191;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue191 extends AbstractTest {

  @Test
  public void commentWithOneVar() throws IOException {
    shouldCompileTo("{{!--{{var}}--}}", $, "");
  }

  @Test
  public void commentWithComplexExpressions() throws IOException {
    shouldCompileTo("{{!--\n" +
        "{{#each names}}\n" +
        "<span>{{first}}</span> <span>{{last}}</span>\n" +
        "{{/each}}\n" +
        "--}}", $, "");
  }

  @Test
  public void commentWithTwoVars() throws IOException {
    shouldCompileTo("{{!--\n" +
        "<span>{{first}}</span> <span>{{last}}</span>\n" +
        "--}}", $, "");
  }
}
