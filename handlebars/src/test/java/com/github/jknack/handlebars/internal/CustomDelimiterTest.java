/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class CustomDelimiterTest extends AbstractTest {
  @Test
  public void block() throws Exception {
    assertEquals(
        "`*`#test`*`inside`*`/test`*`",
        compile("{{=`*` `*`=}}`*`#test`*`inside`*`/test`*`").text());
  }

  @Test
  public void partial() throws Exception {
    assertEquals("^^>test%%", compile("{{=^^ %%=}}^^>test%%", $(), $("test", "")).text());
  }

  @Test
  public void variable() throws Exception {
    assertEquals("+-+test+-+", compile("{{=+-+ +-+=}}+-+test+-+").text());
  }

  @Test
  public void variableUnescaped() throws Exception {
    assertEquals("+-+&test+-+", compile("{{=+-+ +-+=}}+-+&test+-+").text());
  }

  @Test
  public void tripleVariable() throws Exception {
    assertEquals("+-+{test}-+-", compile("{{=+-+ -+-=}}+-+{test}-+-").text());
  }
}
