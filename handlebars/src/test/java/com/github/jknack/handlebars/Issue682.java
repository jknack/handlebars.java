package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.Test;

public class Issue682 extends AbstractTest {
  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.with((value, chain) -> {
        return new Handlebars.SafeString(value.toString());
    });
  }

  @Test
  public void dowork() throws IOException {
    Template t = compile("{{this}}");
    // the formatter (wrongly) trusts all values in the context but proves that it's working
    assertEquals("as\"df", t.apply("as\"df"));
  }
}
