package com.github.jknack.handlebars.i311;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue311 extends AbstractTest {

  @Test
  public void propertyWithPeriod() throws Exception {
    shouldCompileTo("{{ this.[foo.bar] }}", $("foo.bar", "baz"), "baz");
  }

}
