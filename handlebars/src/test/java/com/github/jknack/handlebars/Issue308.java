package com.github.jknack.handlebars;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class Issue308 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers(new File("src/test/resources/issue308.js"));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  public void dowork() throws IOException {
    shouldCompileTo(
        "{{#dowork root/results}}name:{{name}}, age:{{age}}, newval:{{newval}} {{/dowork}}",
        $("root",
            $("results", new Object[]{$("name", "edgar", "age", 34), $("name", "pato", "age", 34) })),
        "name:edgar, age:34, newval:colleague name:pato, age:34, newval:friend ");
  }

}
