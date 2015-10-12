package com.github.jknack.handlebars.i397;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;

public class Issue397 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelpers(this);
  }

  public String concat(final String a, final String b) {
    return a + "+" + b;
  }

  public String helper(final String var, final Options options) {
    return var + options.hash("param1");
  }

  @Test
  public void subexpressionSerializedToPlainText() throws IOException {
    shouldCompileTo("{{helper context_var param1=(concat \"a\" \"b\")}}", $("context_var", "!"),
        "!a+b");
  }

  @Test
  public void subexpressionSerializedToPlainTextHashToString() throws IOException {
    assertEquals("{{helper context_var param1=(concat \"a\" \"b\")}}",
        compile("{{helper context_var param1=(concat \"a\" \"b\")}}").text());
  }

  @Test
  public void subexpressionSerializedToPlainTextParamToString() throws IOException {
    assertEquals("{{helper (concat \"a\" \"b\")}}",
        compile("{{helper (concat \"a\" \"b\")}}").text());
  }

}
