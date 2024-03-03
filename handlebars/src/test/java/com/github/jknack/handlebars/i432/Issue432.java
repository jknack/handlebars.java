/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i432;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue432 extends v4Test {
  @Test
  public void withShouldSupportAs() throws IOException {
    shouldCompileTo(
        "{{#with this as |foo|}}\n" + "    Foo: {{foo.baz}} \n" + "{{/with}}",
        $("hash", $("baz", "foo")),
        "\n" + "    Foo: foo \n" + "");
  }
}
