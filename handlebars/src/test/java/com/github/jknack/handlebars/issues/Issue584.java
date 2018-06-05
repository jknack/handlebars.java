package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

import java.io.IOException;

public class Issue584 extends v4Test {

  @Override protected void configure(Handlebars handlebars) {
    handlebars.setPrettyPrint(true);
  }

  @Test
  public void shouldRemoveBlankAroundElse()
      throws IOException {
    shouldCompileTo("A\n"
            + "{{#if someVariableWhichIsFalse}}\n"
            + "B\n"
            + "{{else}}\n"
            + "C\n"
            + "{{/if}}\n"
            + "D", $, "A\n"
        + "C\n"
        + "D");

    shouldCompileTo("A\n"
        + "{{#if someVariableWhichIsFalse}}\n"
        + "B\n"
        + "{{^}}\n"
        + "C\n"
        + "{{/if}}\n"
        + "D", $, "A\n"
        + "C\n"
        + "D");
  }

}
