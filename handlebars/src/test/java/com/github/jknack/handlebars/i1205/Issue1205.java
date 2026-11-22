/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i1205;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue1205 extends v4Test {

  @Test
  public void withInsideEachWithBlockParamsShouldResolveChildProperty() throws IOException {
    shouldCompileTo(
        "{{#each properties as |prop propName|}}{{#with prop.arrayItems}}"
            + "{{typeName}}|{{this.typeName}}{{/with}}{{/each}}",
        $(
            "hash",
            $(
                "properties",
                $("field", $("typeName", "ParentType", "arrayItems", $("typeName", "ChildType"))))),
        "ChildType|ChildType");
  }

  @Test
  public void blockParamsShouldStillBeVisibleInsideWith() throws IOException {
    shouldCompileTo(
        "{{#each properties as |prop propName|}}{{#with prop.arrayItems}}"
            + "{{propName}}:{{prop.typeName}}:{{typeName}}{{/with}}{{/each}}",
        $(
            "hash",
            $(
                "properties",
                $("field", $("typeName", "ParentType", "arrayItems", $("typeName", "ChildType"))))),
        "field:ParentType:ChildType");
  }

  @Test
  public void blockParamShouldWinOverChildProperty() throws IOException {
    shouldCompileTo(
        "{{#each items as |item|}}{{#with item.child}}{{item.child.item}}{{/with}}{{/each}}",
        $("hash", $("items", $("a", $("child", $("item", "ChildItem"))))),
        "ChildItem");
  }
}
