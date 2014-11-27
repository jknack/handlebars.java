package com.github.jknack.handlebars;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class Issue327 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers(new File("src/test/resources/issue327.js"));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  public void link() throws IOException {
    shouldCompileTo("{{link 'handlebars.java' 'https://github.com/jknack/handlebars.java'}}", $,
        "<a href=\"https://github.com/jknack/handlebars.java\">handlebars.java</a>");
  }

  @Test
  public void calllink() throws IOException {
    shouldCompileTo("{{call-link 'handlebars.java' 'https://github.com/jknack/handlebars.java'}}", $,
        "<a href=\"https://github.com/jknack/handlebars.java\">handlebars.java</a>");
  }

}
