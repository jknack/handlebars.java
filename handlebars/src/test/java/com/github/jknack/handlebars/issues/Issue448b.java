package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.v4Test;

public class Issue448b extends v4Test {

  public CharSequence is(final Object value, final Object test, final Options options)
      throws IOException {
    if (value.toString().equals(test)) {
      return options.fn();
    } else {
      return options.inverse();
    }
  }

  public CharSequence remainder(final int value, final int divisor) {
    return Integer.toString(value % divisor);
  }

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelpers(this);
  }

  @Test
  public void shouldApplySubOnBlockParams() throws IOException {
    shouldCompileTo(
        "{{#each this as |c|}} {{c}} - {{#is 0 (remainder c 2)}}even{{else}} odd{{/is}} \n" +
            "{{/each}}",
        $("hash", new Object[]{4, 5, 6, 7, 8 }),
        " 4 - even \n" +
        " 5 -  odd \n" +
        " 6 - even \n" +
        " 7 -  odd \n" +
        " 8 - even \n");
  }

}
