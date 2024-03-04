/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;

public class AssignHelperTest extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelper(AssignHelper.NAME, AssignHelper.INSTANCE);
  }

  @Test
  public void assignResult() throws IOException {
    shouldCompileTo(
        "{{#assign \"benefitsTitle\"}} benefits.{{type}}.title {{/assign}}",
        $("type", "discounts"),
        "");
  }

  @Test
  public void assignContext() throws IOException {
    Context context = Context.newContext($("type", "discounts"));

    shouldCompileTo(
        "{{#assign \"benefitsTitle\"}} benefits.{{type}}.title {{/assign}}", context, "");

    assertEquals("benefits.discounts.title", context.data("benefitsTitle"));
  }
}
