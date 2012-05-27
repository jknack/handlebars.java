package com.github.edgarespina.handlebars.parser;

import java.util.HashMap;
import java.util.Map;

public class TrueValueTest extends TemplateTest {

  @Override
  public String input() {
    return "{{#none}}There is one{{/none}}";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("none", true);
    return scope;
  }

  @Override
  public String output() {
    return "There is one";
  }
}
