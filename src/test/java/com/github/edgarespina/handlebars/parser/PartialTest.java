package com.github.edgarespina.handlebars.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.edgarespina.handlerbars.ResourceLocator;

public class PartialTest extends TemplateTest {

  @Override
  public ResourceLocator resourceLocator() {
    return new ResourceLocator() {

      @Override
      protected Reader read(final String uri) throws IOException {
        if ("template.html".equals(uri)) {
          return new StringReader(input());
        }
        if ("user".equals(uri)) {
          return new StringReader(user());
        }
        return new StringReader("<b>{{lastName}}</b>");
      }
    };
  }

  protected String user() {
    return "<strong>{{firstName}}{{> details}}</strong>";
  }

  @Override
  public String input() {
    return "<h2>Names</h2>{{#names}}{{> user}}{{/names}}";
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

    scope.put("names", Arrays.asList(john, peter));
    return scope;
  }

  @Override
  public String output() {
    return "<h2>Names</h2>" +
        "<strong>John<b>Doe</b></strong>" +
        "<strong>Peter<b>Doe</b></strong>";
  }
}
