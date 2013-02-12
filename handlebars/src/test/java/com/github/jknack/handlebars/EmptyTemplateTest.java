package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

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
}
