package com.github.edgarespina.handlebars.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimpleListTest extends TemplateTest {

  @Override
  public String template() {
    return "{{#list}}{{.}}{{/list}}";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("list", Arrays.asList("A", "B", "C"));
    return scope;
  }

  @Override
  public String output() {
    return "ABC";
  }
}
