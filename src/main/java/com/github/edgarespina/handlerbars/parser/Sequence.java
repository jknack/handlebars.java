package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.edgarespina.handlerbars.Scope;
import com.github.edgarespina.handlerbars.Template;

class Sequence extends BaseTemplate implements Iterable<BaseTemplate> {

  private final List<BaseTemplate> nodes = new ArrayList<BaseTemplate>();

  public Sequence() {
  }

  public boolean add(final BaseTemplate node) {
    boolean add = true;
    if (node instanceof Sequence) {
      add = ((Sequence) node).nodes.size() > 0;
    }
    if (add) {
      nodes.add(node);
    }
    return add;
  }

  @Override
  public void merge(final Scope scope, final Writer writer) throws IOException {
    for (Template node : nodes) {
      node.merge(scope, writer);
    }
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    for (Template node : nodes) {
      buffer.append(node);
    }
    return buffer.toString();
  }

  @Override
  public Iterator<BaseTemplate> iterator() {
    return nodes.iterator();
  }

  @Override
  public boolean remove(final Template child) {
    boolean removed = nodes.remove(child);
    if (!removed) {
      Iterator<BaseTemplate> iterator = nodes.iterator();
      while (!removed && iterator.hasNext()) {
        removed = iterator.next().remove(child);
      }
    }
    return removed;
  }

}
