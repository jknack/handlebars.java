/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

/** https://github.com/jknack/handlebars.java/issues/657 */
public class Issue657 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {

    try {
      handlebars.registerHelpers(new File("src/test/resources/issue657.js"));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  public void shouldAllowES6LetOrConstLiterals() throws Exception {

    shouldCompileTo(
        "{{#and great magnificent}}Hello 657{{/and}}",
        $("great", true, "magnificent", true),
        "Hello 657");
  }
}
