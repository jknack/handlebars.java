package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

public class Issue660 extends v4Test {

  @Test
  public void partialWithLineBreak() throws Exception {
    shouldCompileTo("text text text\n"
            + "{{#>some-partial param1=\"val1\"\n"
            + "                 param2=\"val2\"}}\n"
            + "    default content\n"
            + "{{/some-partial}}", $, "text text text\n"
        + "\n"
        + "    default content\n");
  }

}
