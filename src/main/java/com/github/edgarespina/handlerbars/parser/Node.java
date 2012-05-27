package com.github.edgarespina.handlerbars.parser;

import java.util.Map;

public abstract class Node {

  public abstract void
      toString(StringBuilder output, Map<String, Object> scope);
}
