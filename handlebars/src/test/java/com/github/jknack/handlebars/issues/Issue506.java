package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.v4Test;

public class Issue506 extends v4Test {

  @Test
  public void nestedAs() throws IOException {
    shouldCompileTo("{{#each things}}" +
        "not using \"as\": value is {{this}}, key is {{@index}}, hello is {{lower \"HELLO\"}}" +
        "{{/each}}",
        $("hash", $("things", new String[]{"foo" }),
            "helpers", $("lower", new Helper<String>() {
              @Override
              public Object apply(final String str, final Options options) throws IOException {
                return str.toLowerCase();
              }
            })),
        "not using \"as\": value is foo, key is 0, hello is hello");

    shouldCompileTo("{{#each things as |value key|}}" +
        "using \"as\": value is {{value}}, key is {{key}}, hello is {{lower \"HELLO\"}}" +
        "{{/each}}",
        $("hash", $("things", new String[]{"foo" }),
            "helpers", $("lower", new Helper<String>() {
              @Override
              public Object apply(final String str, final Options options) throws IOException {
                return str.toLowerCase();
              }
            })),
        "using \"as\": value is foo, key is 0, hello is hello");

    shouldCompileTo("{{#each things as |value key|}}" +
        "using \"as\" and \"#\": value is {{value}}, key is {{key}}, hello is {{#lower \"HELLO\"}}{{this}}{{/lower}}" +
        "{{/each}}",
        $("hash", $("things", new String[]{"foo" }),
            "helpers", $("lower", new Helper<String>() {
              @Override
              public Object apply(final String str, final Options options) throws IOException {
                return str.toLowerCase();
              }
            })),
        "using \"as\" and \"#\": value is foo, key is 0, hello is hello");
  }

}
