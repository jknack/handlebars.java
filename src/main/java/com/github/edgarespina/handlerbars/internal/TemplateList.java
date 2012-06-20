package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.edgarespina.handlerbars.Template;

/**
 * A list of templates.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class TemplateList extends BaseTemplate
    implements Iterable<BaseTemplate> {

  /**
   * The list of child templates.
   */
  private final List<BaseTemplate> nodes = new ArrayList<BaseTemplate>();

  /**
   * Add a child template. Empty templates aren't added.
   *
   * @param child The childe template.
   * @return True, if the template was added.
   */
  public boolean add(final BaseTemplate child) {
    boolean add = true;
    BaseTemplate candidate = child;
    if (candidate instanceof TemplateList) {
      TemplateList sequence = (TemplateList) candidate;
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
  public void merge(final Context scope, final Writer writer)
      throws IOException {
    for (BaseTemplate node : nodes) {
      node.merge(scope, writer);
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

  /**
   * The number of children.
   *
   * @return The number of children.
   */
  public int size() {
    return nodes.size();
  }

}
