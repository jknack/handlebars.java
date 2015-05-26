package com.github.jknack.handlebars.i376;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.StringHelpers;

public class Issue376 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelpers(StringHelpers.class);
  }

  @Test
  public void noThis() throws IOException {
    shouldCompileTo("{{#each foo}}" +
        "{{#if this.price}}{{numberFormat price 'currency'}}\n{{/if}}" +
        "{{/each}}", $("foo", new Object[]{$("price", 5), $("price", 7) }), "$5.00\n$7.00\n");
  }

  @Test
  public void withThis() throws IOException {
    shouldCompileTo("{{#each foo}}" +
        "{{#if this.price}}{{numberFormat this.price 'currency'}}\n{{/if}}" +
        "{{/each}}", $("foo", new Object[]{$("price", 5), $("price", 7) }), "$5.00\n$7.00\n");
  }

}
