package com.github.edgarespina.handlebars.parser;

import java.util.HashMap;
import java.util.Map;

public class HtmlVariableTest extends TemplateTest {

  @Override
  public String input() {
    return "Hello {{name}}!";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("name", "<b>John</b>");
    return scope;
  }

  @Override
  public String output() {
    return "Hello &lt;b&gt;John&lt;/b&gt;!";
  }
}
