package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class GlobalDelimsTest extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    return super.newHandlebars().startDelimiter("<<").endDelimiter(">>");
  }

  @Test
  public void customDelims() throws IOException {
    shouldCompileTo("<<hello>>", $("hello", "hi"), "hi");
  }
}
