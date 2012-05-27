package com.github.edgarespina.handlebars.parser;

import java.util.HashMap;
import java.util.Map;

public class SectionTest extends TemplateTest {

  @Override
  public String input() {
    return
    "{{#person}}" +
        "Hi {{firstName}} {{lastName}}!" +
    "{{/person}}";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    Map<String, Object> person = new HashMap<String, Object>();
    person.put("firstName", "John");
    person.put("lastName", "Doe");
    scope.put("person", person);
    return scope;
  }

  @Override
  public String output() {
    return "Hi John Doe!";
  }
}
