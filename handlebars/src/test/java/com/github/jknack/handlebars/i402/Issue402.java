package com.github.jknack.handlebars.i402;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue402 extends AbstractTest {

  @Test
  public void shouldCleanupPreviousPartialValues() throws IOException {
    shouldCompileToWithPartials("{{> user name=\"Bob\" age=\"31\"}}\n{{> user age=\"29\"}}", $,
        $("user", "<div><b>{{name}}</b> {{age}}</div>"), "<div><b>Bob</b> 31</div>\n" +
            "<div><b></b> 29</div>");
  }
}
