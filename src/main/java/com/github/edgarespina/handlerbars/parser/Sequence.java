package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.edgarespina.handlerbars.Template;

class Sequence extends Template implements Iterable<Template> {

  private final List<Template> nodes = new ArrayList<Template>();

  public Sequence() {
  }

  public Sequence add(final Template node) {
    nodes.add(node);
    return this;
  }

  public Sequence remove(final Template node) {
    nodes.remove(node);
    return this;
  }

  @Override
  public void merge(final Map<String, Object> scope, final Writer writer) throws IOException {
    for(Template node: nodes) {
      node.merge(scope, writer);
    }
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    for(Template node: nodes) {
      buffer.append(node);
    }
    return buffer.toString();
  }

  @Override
  public Iterator<Template> iterator() {
    return nodes.iterator();
  }
}
