/**
 * Copyright (c) 2012-2013 Edgar Espina
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;

/**
 * A list of templates.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class TemplateList extends BaseTemplate implements Iterable<Template> {

  /**
   * The list of child templates.
   */
  private final List<Template> nodes = new LinkedList<Template>();

  /**
   * Add a child template. Empty templates aren't added.
   *
   * @param child The child template.
   * @return True, if the template was added.
   */
  public boolean add(final Template child) {
    nodes.add(child);
    return true;
  }

  @Override
  protected void merge(final Context context, final Writer writer)
      throws IOException {
    for (Template node : nodes) {
      node.apply(context, writer);
    }
  }

  @Override
  public String text() {
    StringBuilder buffer = new StringBuilder();
    for (Template node : nodes) {
      buffer.append(node.text());
    }
    return buffer.toString();
  }

  @Override
  public Iterator<Template> iterator() {
    return nodes.iterator();
  }

  /**
   * Remove a node from the given position.
   *
   * @param idx The node's position.
   */
  public void remove(final int idx) {
    nodes.remove(idx);
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
