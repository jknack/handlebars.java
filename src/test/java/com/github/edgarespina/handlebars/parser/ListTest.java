package com.github.edgarespina.handlebars.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ListTest extends TemplateTest {

  @Override
  public String template() {
    return "{{#list}}{{firstName}} {{lastName}}, {{/list}}";
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    Map<String, Object> john = new HashMap<String, Object>();
    john.put("firstName", "John");
    john.put("lastName", "Doe");

    Map<String, Object> peter = new HashMap<String, Object>();
    peter.put("firstName", "Peter");
    peter.put("lastName", "Doe");

    scope.put("list", Arrays.asList(john, peter));
    return scope;
  }

  @Override
  public String output() {
    return "John Doe, Peter Doe, ";
  }
}
