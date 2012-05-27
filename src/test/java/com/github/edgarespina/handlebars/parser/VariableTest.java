package com.github.edgarespina.handlebars.parser;

import java.util.HashMap;
import java.util.Map;

public class VariableTest extends TemplateTest {

  @Override
  public String input() {
    return "Hello {{name}}!";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("name", "John");
    return scope;
  }

  @Override
  public String output() {
    return "Hello John!";
  }
}
