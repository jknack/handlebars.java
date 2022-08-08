package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.StringHelpers;

public class Issue714 extends v4Test {

  @Override protected void configure(Handlebars handlebars) {
    handlebars.registerHelpers(StringHelpers.class);
  }

  @Test
  public void shouldIgnoreEmptyString() throws IOException {
    shouldCompileTo("{{cut value \"-\"}}", $("hash", $("value", "")), "");

    shouldCompileTo("{{cut value \"-\"}}", $("hash", $("value", "2019-12-30")), "20191230");
  }
}
