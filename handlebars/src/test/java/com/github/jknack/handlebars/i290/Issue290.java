/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i290;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue290 extends AbstractTest {

  @Test
  public void identifiersContainingDigitsAndHyphenMustNotFail() throws IOException {
    assertNotNull(compile("{{#each article-1-column}}{{/each}}"));
    assertNotNull(compile("{{#each article-1column}}{{/each}}"));
  }
}
