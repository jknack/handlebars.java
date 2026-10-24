/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue1078 extends v4Test {

  @Test
  public void shouldResolveIdentifiersThatStartWithDigits() throws IOException {
    shouldCompileTo(
        "<p>{{24HrServicePh}}</p>",
        $("hash", $("24HrServicePh", "1-800-000-0000")),
        "<p>1-800-000-0000</p>");
  }

  @Test
  public void shouldResolveDigitLeadingIdentifiersInBlocks() throws IOException {
    shouldCompileTo("{{#if 24Hr}}yes{{/if}}", $("hash", $("24Hr", true)), "yes");
  }

  @Test
  public void shouldStillParseNumericLiteralsAsParameters() throws IOException {
    shouldCompileTo("{{foo 24}} {{foo 24.5}}", $("helpers", $("foo", "ok")), "ok ok");
  }
}
