package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Issue654 extends v4Test {

  @Test
  public void makeSureMapResolverIsAlwaysPresent() throws Exception {
    Context ctx = Context.newBuilder($("foo", "bar"))
        .resolver(JavaBeanValueResolver.INSTANCE)
        .build();
    assertEquals("bar", ctx.get("foo"));
  }

  @Test
  public void ignoreMapResolverWhenItIsProvided() throws Exception {
    Context ctx = Context.newBuilder($("foo", "bar"))
        .resolver(MapValueResolver.INSTANCE)
        .build();
    assertEquals("bar", ctx.get("foo"));
  }

}
