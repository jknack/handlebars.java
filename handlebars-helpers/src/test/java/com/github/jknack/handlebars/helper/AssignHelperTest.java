package com.github.jknack.handlebars.helper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

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
        $("type", "discounts"), "");
  }

  @Test
  public void assignContext() throws IOException {
    Context context = Context.newContext($("type", "discounts"));

    shouldCompileTo("{{#assign \"benefitsTitle\"}} benefits.{{type}}.title {{/assign}}",
        context, "");

    assertEquals("benefits.discounts.title", context.data("benefitsTitle"));
  }

}
