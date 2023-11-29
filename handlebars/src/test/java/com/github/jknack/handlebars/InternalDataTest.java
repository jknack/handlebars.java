package com.github.jknack.handlebars;

import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InternalDataTest extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("printFooAndBar", (context, options) ->
            String.format("%s %s", options.context.data("foo"), options.context.internalData("bar")));
    return handlebars;
  }

  @Override
  protected Object configureContext(final Object model) {
    return Context.newBuilder(model)
        .resolver(
                MapValueResolver.INSTANCE,
                JavaBeanValueResolver.INSTANCE,
                FieldValueResolver.INSTANCE,
                MethodValueResolver.INSTANCE)
        .build()
        .data("foo", "foo")
        .internalData("bar", "bar");
  }

  @Test
  public void dataAvailableForRendering() throws IOException {
    shouldCompileTo("{{foo}}", "", "foo");
    assertEquals("foo", ((Context)configureContext("")).get("foo"));
  }

  @Test
  public void internalDataNotAvailableForRendering() throws IOException {
    shouldCompileTo("{{bar}}", "", "");
    shouldCompileTo("{{./bar}}", "", "");
    shouldCompileTo("{{../bar}}", "", "");
    shouldCompileTo("{{.././bar}}", "", "");
    shouldCompileTo("{{this.bar}}", "", "");
    shouldCompileTo("{{internalData}}", "", "");
    shouldCompileTo("{{internalData.bar}}", "", "");
    shouldCompileTo("{{this.internalData}}", "", "");
    shouldCompileTo("{{this.internalData.bar}}", "", "");
    assertNull(((Context)configureContext("")).get("bar"));
  }

  @Test
  public void helperAbleToAccessInternalData() throws IOException {
    shouldCompileTo("{{printFooAndBar}}", "", "foo bar");
  }
}
