/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue476 extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers(
          "helpers.js",
          "Handlebars.registerHelper('length', function (array) {"
              + "return (!array) ? 0 : array.length.toFixed();"
              + "});");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Test
  public void jsLengthMisbehaviorWithLists() throws IOException {
    shouldCompileTo(
        "{{length this}}", $("hash", new ArrayList<>(Arrays.asList("1", "3", "4"))), "3");
  }

  @Test
  public void jsLengthMisbehaviorWithArray() throws IOException {
    shouldCompileTo("{{length this}}", $("hash", new String[] {"1", "3", "4"}), "3");
  }
}
