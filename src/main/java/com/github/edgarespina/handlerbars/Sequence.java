package com.github.edgarespina.handlerbars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Sequence extends Node implements Iterable<Node> {

  private final List<Node> nodes = new ArrayList<Node>();

  public Sequence() {
  }

  public Sequence add(final Node node) {
    nodes.add(node);
    return this;
  }

  public Sequence remove(final Node node) {
    nodes.remove(node);
    return this;
  }

  @Override
  public void toString(final StringBuilder output, final Map<String, Object> scope) {
    for(Node node: nodes) {
      node.toString(output, scope);
    }
  }

  @Override
  public Iterator<Node> iterator() {
    return nodes.iterator();
  }
}
