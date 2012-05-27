package com.github.edgarespina.handlebars.parser;

import java.util.Collections;
import java.util.Map;

public class CommentTest extends TemplateTest {

  @Override
  public String input() {
    return "<h1>Today{{! ignore me }}.</h1>";
  }

  @Override
  public Map<String, Object> scope() {
    return Collections.emptyMap();
  }

  @Override
  public String output() {
    return "<h1>Today.</h1>";
  }
}
