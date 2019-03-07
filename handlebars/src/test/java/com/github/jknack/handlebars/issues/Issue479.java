package com.github.jknack.handlebars.issues;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue479 extends v4Test {
  @Test
  public void partialWithHashOverride() throws Exception {
    shouldCompileTo("{{> dude _greeting=\"Hello\"}}",
        $("hash", $("name", "Elliot", "_greeting", "Good Morning"),
            "partials", $("dude", "{{_greeting}} {{name}}!")),
        "Hello Elliot!");
  }

  @Test
  public void partialWithHash() throws Exception {
    shouldCompileTo("{{> dude}}",
        $("hash", $("name", "Elliot", "_greeting", "Good Morning"),
            "partials", $("dude", "{{_greeting}} {{name}}!")),
        "Good Morning Elliot!");
  }
}
