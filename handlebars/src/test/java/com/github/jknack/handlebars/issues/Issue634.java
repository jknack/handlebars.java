package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.v4Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class Issue634 extends v4Test {

  @Test
  public void shouldThrowHandlebarsExceptionWhenPartialBlockIsMissing() throws Exception {
    try {
      shouldCompileTo("{{> my-partial}}",
          $("partials", $("my-partial", "Hello {{> @partial-block}}")), null);
      fail("Must throw HandlebarsException");
    } catch (HandlebarsException x) {
      assertTrue(x.getMessage().contains("does not provide a @partial-block"));
    }
  }

  @Test
  public void shouldNotThrowHandlebarsException() throws Exception {
    shouldCompileTo("{{#> my-partial}}634{{/my-partial}}",
        $("partials", $("my-partial", "Hello {{> @partial-block}}")), "Hello 634");
  }

}
