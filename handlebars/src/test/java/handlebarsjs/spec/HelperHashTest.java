/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package handlebarsjs.spec;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class HelperHashTest extends AbstractTest {

  @Test
  public void providingHelperHash() throws IOException {
    shouldCompileTo(
        "Goodbye {{cruel}} {{world}}!",
        $("cruel", "cruel"),
        $("world", "world"),
        "Goodbye cruel world!",
        "helpers hash is available");

    shouldCompileTo(
        "Goodbye {{#iter}}{{cruel}} {{world}}{{/iter}}!",
        $("iter", new Object[] {$("cruel", "cruel")}),
        $("world", "world"),
        "Goodbye cruel world!",
        "helpers hash is available inside other blocks");
  }

  @Test
  public void inCaseOsfConflictTheExplicitHashWins() throws IOException {}

  @Test
  public void theHelpersClassIsAvailableInNestedContexts() throws IOException {}
}
