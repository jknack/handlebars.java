package com.github.edgarespina.handlebars.parser;

import java.util.HashMap;
import java.util.Map;

public class FalseValueTest extends TemplateTest {

  @Override
  public String template() {
    return "{{#none}}None{{/none}}";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("none", false);
    return scope;
  }

  @Override
  public String output() {
    return "";
  }
}
