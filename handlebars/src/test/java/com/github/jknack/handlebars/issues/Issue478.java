package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

import java.io.IOException;

public class Issue478 extends v4Test {

  public static class Foo implements EscapingStrategy {

    @Override public CharSequence escape(CharSequence value) {
      return value.toString().replace("foo", "bar");
    }
  }

  public static class Bar implements EscapingStrategy {

    @Override public CharSequence escape(CharSequence value) {
      return value.toString().replace("bar", "$bar$");
    }
  }

  @Override protected void configure(Handlebars handlebars) {
    handlebars.with(new Foo(), new Bar());
  }

  @Test
  public void shouldAllowToChainEscapeStrategy() throws IOException {
    shouldCompileTo("{{var}}", $("hash", $("var", "foo")), "$bar$");
  }

}
