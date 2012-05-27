package com.github.edgarespina.handlebars.parser;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveArrayTest extends TemplateTest {

  @Override
  public String input() {
    return "{{#list}}{{.}} {{/list}}";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("list", new int[] {1, 2, 3, 4, 5});
    return scope;
  }

  @Override
  public String output() {
    return "1 2 3 4 5 ";
  }
}
