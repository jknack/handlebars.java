/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

public class EmptyTemplateTest {

  @Test
  public void text() {
    assertEquals("", Template.EMPTY.text());
  }

  @Test
  public void apply() throws IOException {
    assertEquals("", Template.EMPTY.apply((Object) null));
    assertEquals("", Template.EMPTY.apply((Context) null));
  }

  @Test
  public void applyWithWriter() throws IOException {
    Template.EMPTY.apply((Object) null, null);
    Template.EMPTY.apply((Context) null, null);
  }

  @Test
  public void toJs() throws IOException {
    assertEquals("", Template.EMPTY.toJavaScript());
  }

  @Test
  public void typeSafeTemplate() throws IOException {
    TypeSafeTemplate<Object> ts = Template.EMPTY.as();
    assertNotNull(ts);
    assertEquals("", ts.apply(null));
    StringWriter writer = new StringWriter();
    ts.apply(null, writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void collect() throws IOException {
    assertNotNull(Template.EMPTY.collect());
    assertEquals(0, Template.EMPTY.collect().size());
  }
}
