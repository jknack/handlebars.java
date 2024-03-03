/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.v4Test;

public class Issue654 extends v4Test {

  @Test
  public void makeSureMapResolverIsAlwaysPresent() throws Exception {
    Context ctx =
        Context.newBuilder($("foo", "bar")).resolver(JavaBeanValueResolver.INSTANCE).build();
    assertEquals("bar", ctx.get("foo"));
  }

  @Test
  public void ignoreMapResolverWhenItIsProvided() throws Exception {
    Context ctx = Context.newBuilder($("foo", "bar")).resolver(MapValueResolver.INSTANCE).build();
    assertEquals("bar", ctx.get("foo"));
  }
}
