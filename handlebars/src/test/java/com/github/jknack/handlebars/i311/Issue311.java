/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i311;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue311 extends AbstractTest {

  @Test
  public void propertyWithPeriod() throws Exception {
    shouldCompileTo("{{ this.[foo.bar] }}", $("foo.bar", "baz"), "baz");
  }
}
