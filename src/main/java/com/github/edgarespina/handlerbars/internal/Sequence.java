package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.edgarespina.handlerbars.Template;

class Sequence extends BaseTemplate implements Iterable<BaseTemplate> {

  private final List<BaseTemplate> nodes = new ArrayList<BaseTemplate>();

  public Sequence() {
  }

  public boolean add(final BaseTemplate node) {
    boolean add = true;
    BaseTemplate candidate = node;
    if (candidate instanceof Sequence) {
      Sequence sequence = (Sequence) candidate;
      if (sequence.size() == 0) {
        add = false;
      } else if (sequence.size() == 1) {
        candidate = sequence.iterator().next();
      }
    }
    if (add) {
      nodes.add(candidate);
    }
    return add;
  }

  @Override
  public void apply(final Scope scope, final Writer writer) throws IOException {
    for (BaseTemplate node : nodes) {
      node.apply(scope, writer);
    }
  }

  @Override
  public String text() {
    StringBuilder buffer = new StringBuilder();
    for (BaseTemplate node : nodes) {
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

  public int size() {
    return nodes.size();
  }

}
