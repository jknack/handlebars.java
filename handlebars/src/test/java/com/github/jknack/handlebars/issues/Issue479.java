package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue479 extends v4Test {
  @Test
  public void partialWithHashOverride() throws IOException {
    shouldCompileTo("{{> dude _greeting=\"Hello\"}}",
        $("hash", $("name", "Elliot", "_greeting", "Good Morning"),
            "partials", $("dude", "{{_greeting}} {{name}}!")),
        "Hello Elliot!");
  }

  @Test
  public void partialWithHash() throws IOException {
    shouldCompileTo("{{> dude}}",
        $("hash", $("name", "Elliot", "_greeting", "Good Morning"),
            "partials", $("dude", "{{_greeting}} {{name}}!")),
        "Good Morning Elliot!");
  }
}
