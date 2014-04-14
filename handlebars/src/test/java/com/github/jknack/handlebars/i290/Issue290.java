package com.github.jknack.handlebars.i290;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue290 extends AbstractTest {

  @Test
  public void identifiersContainingDigitsAndHyphenMustNotFail() throws IOException {
    assertNotNull(compile("{{#each article-1-column}}{{/each}}"));
    assertNotNull(compile("{{#each article-1column}}{{/each}}"));
  }
}
