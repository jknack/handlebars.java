package com.github.edgarespina.handlebars.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InvertedSectionTest extends TemplateTest {

  @Override
  public String template() {
    return
    "{{#repo}}" +
        "<b>{{name}}</b>" +
    "{{/repo}}" +
    "{{^repo}}" +
        "No repos :(" +
    "{{/repo}}";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("repo", Collections.emptyList());
    return scope;
  }

  @Override
  public String output() {
    return "No repos :(";
  }
}
