package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

public class Issue552 extends v4Test {

  @Override protected Object configureContext(Object context) {
    return Context.newBuilder(context)
        .resolver(MapValueResolver.INSTANCE,
            FieldValueResolver.INSTANCE,
            JavaBeanValueResolver.INSTANCE,
            MethodValueResolver.INSTANCE)
        .build();
  }

  @Test
  public void shouldKeepContextOnBlockParameter() throws Exception {
    shouldCompileTo("{{> button size='large'}}",
        $("hash", $, "partials", $("button", "<button class=\"button-{{size}}>\n"
            + "    Button with size {{size}}\n"
            + "</button>")),
        "<button class=\"button-large>\n"
            + "    Button with size large\n"
            + "</button>");
  }
}