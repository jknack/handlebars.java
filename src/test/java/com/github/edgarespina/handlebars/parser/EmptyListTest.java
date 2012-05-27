package com.github.edgarespina.handlebars.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EmptyListTest extends TemplateTest {

  @Override
  public String input() {
    return "{{#list}}False{{/list}}";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("list", Collections.emptyList());
    return scope;
  }

  @Override
  public String output() {
    return "";
  }
}
