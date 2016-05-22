package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.v4Test;

public class Issue483 extends v4Test {

  @Test
  public void shouldPassObjectResult() throws IOException {
    shouldCompileTo("{{#if (equal arg 'foo')}}foo{{/if}}",
        $("hash", $("arg", "foo"), "helpers", $("equal", new Helper<String>() {
          @Override
          public Object apply(final String left, final Options options) throws IOException {
            return left.equals(options.param(0));
          }
        })), "foo");

    shouldCompileTo("{{#if (equal arg 'foo')}}foo{{/if}}",
        $("hash", $("arg", "bar"), "helpers", $("equal", new Helper<String>() {
          @Override
          public Object apply(final String left, final Options options) throws IOException {
            return left.equals(options.param(0));
          }
        })), "");
  }

}
