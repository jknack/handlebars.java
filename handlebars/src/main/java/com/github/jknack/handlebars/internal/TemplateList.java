/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;

/**
 * A list of templates.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class TemplateList extends BaseTemplate implements Iterable<BaseTemplate> {

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
  protected void merge(final Context context, final Writer writer)
      throws IOException {
    for (BaseTemplate node : nodes) {
      node.apply(context, writer);
    }
  }

  @Override
  public String text() {
    StringBuilder buffer = new StringBuilder();
    for (BaseTemplate node : nodes) {
      buffer.append(node.text());
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
