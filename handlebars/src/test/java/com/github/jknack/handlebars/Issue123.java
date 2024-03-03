/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class Issue123 extends AbstractTest {

  @Test
  public void spacesInBlock() throws IOException {
    shouldCompileTo("{{#if \"stuff\" }}Bingo{{/if}}", $, "Bingo");
    shouldCompileTo("{{#if \"stuff\"  }}Bingo{{/if}}", $, "Bingo");
    shouldCompileTo("{{#if \"stuff\"}}Bingo{{/if}}", $, "Bingo");

    shouldCompileTo("{{# if \"stuff\"}}Bingo{{/if}}", $, "Bingo");
    shouldCompileTo("{{#if \"stuff\"}}Bingo{{/ if}}", $, "Bingo");
    shouldCompileTo("{{# if \"stuff\" }}Bingo{{/ if }}", $, "Bingo");
  }

  @Test
  public void spacesInVar() throws IOException {
    shouldCompileTo("{{var}}", $, "");
    shouldCompileTo("{{ var}}", $, "");
    shouldCompileTo("{{var }}", $, "");
    shouldCompileTo("{{ var }}", $, "");
    shouldCompileTo("{{var x }}", $, $("var", ""), "");
  }
}
