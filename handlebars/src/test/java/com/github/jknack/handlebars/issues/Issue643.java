package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

public class Issue643 extends v4Test {

  @Test
  public void shouldAllowES6LetOrConstLiterals() throws Exception {
    shouldCompileTo("template: {{empty}} "
            + "{{> partial}}",
        $("partials", $("partial", "partial: {{empty}}"),
            "data", $("empty", false)),
        "template: true partial: true");
  }

}
