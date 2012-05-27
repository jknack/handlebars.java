package com.github.edgarespina.handlebars.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Template;

public class LambdaTest extends TemplateTest {

  @Override
  public String template() {
    return
    "{{#wrapped}}" +
        "{{name}} is awesome." +
        "{{/wrapped}}";
  }

  @Override
  public Map<String, Object> scope() {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("name", "Willy");
    scope.put("wrapped", new Lambda() {
      @Override
      public String apply(final Template template,
          final Map<String, Object> scope) throws IOException {
        return "<b>" +  template.merge(scope) + "</b>";
      }
    });
    return scope;
  }

  @Override
  public String output() {
    return "<b>Willy is awesome.</b>";
  }
}
