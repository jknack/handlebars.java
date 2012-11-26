package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SwitchPartialContextTest {

  @Test
  public void switchPartialContext() throws IOException {
    String partial = "{{name}}";
    Handlebars handlebars = new Handlebars(new MapTemplateLoader().define("partial", partial));

    Map<String, Object> root = new HashMap<String, Object>();
    root.put("name", "root");

    Map<String, Object> context = new HashMap<String, Object>();
    context.put("name", "context");
    root.put("context", context);

    Map<String, Object> child = new HashMap<String, Object>();
    child.put("name", "child");
    context.put("child", child);

    assertEquals("root", handlebars.compile("{{>partial}}").apply(root));
    assertEquals("root", handlebars.compile("{{>partial this}}").apply(root));
    assertEquals("context", handlebars.compile("{{>partial context}}").apply(root));
    assertEquals("", handlebars.compile("{{>partial context.name}}").apply(root));
    assertEquals("child", handlebars.compile("{{>partial context.child}}").apply(root));
  }

  @Test
  public void partialWithContext() throws IOException {
    String partial = "{{#this}}{{name}} {{/this}}";
    Handlebars handlebars = new Handlebars(new MapTemplateLoader().define("dude", partial));

    Map<String, Object> root = new HashMap<String, Object>();

    Map<String, Object> moe = new HashMap<String, Object>();
    moe.put("name", "moe");

    Map<String, Object> curly = new HashMap<String, Object>();
    curly.put("name", "curly");

    root.put("dudes", new Object[]{moe, curly });

    assertEquals("Dudes: moe curly ", handlebars.compile("Dudes: {{>dude dudes}}").apply(root));
  }
}
