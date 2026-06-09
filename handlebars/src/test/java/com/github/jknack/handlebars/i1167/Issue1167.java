/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i1167;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue1167 extends v4Test {

  @Test
  public void shouldResolveChildProperty() throws IOException {
    shouldCompileTo(
        "{{typeName}}{{#with prop.arrayItems}} {{typeName}}{{/with}}",
        $("hash", $("typeName", "ParentType", "prop", $("arrayItems", $("typeName", "ChildType")))),
        "ParentType ChildType");
  }

  record CodegenType(String typeName) {}

  @Test
  public void shouldResolveChildPropertyOnRecord() throws IOException {
    shouldCompileTo(
        "{{typeName}}{{#with prop.arrayItems}} {{typeName}}{{/with}}",
        $(
            "hash",
            $("typeName", "ParentType", "prop", $("arrayItems", new CodegenType("ChildType")))),
        "ParentType ChildType");
  }
}
