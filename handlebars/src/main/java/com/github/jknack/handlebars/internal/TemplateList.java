/**
 * Copyright (c) 2012-2015 Edgar Espina
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.TagType;
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
  private final List<Template> nodes = new ArrayList<>();

  /** Keep track of direct decorators and run them before merge. */
  private final List<BaseTemplate> decorators = new ArrayList<>();

  /** True, if this block has decorators. */
  private boolean decorate;

  /**
   * Creates a new template list.
   *
   * @param handlebars A handlebars instance. Required.
   */
  public TemplateList(final Handlebars handlebars) {
    super(handlebars);
  }

  /**
   * Add a child template. Empty templates aren't added.
   *
   * @param child The child template.
   * @return True, if the template was added.
   */
  public boolean add(final Template child) {
    nodes.add(child);
    if (child instanceof VarDecorator || child instanceof BlockDecorator
        || child instanceof Partial) {
      decorators.add((BaseTemplate) child);
      decorate = true;
    }
    return true;
  }

  @Override
  public void before(final Context context, final Writer writer) throws IOException {
    for (BaseTemplate node : decorators) {
      node.before(context, writer);
    }
  }

  @Override
  public void after(final Context context, final Writer writer) throws IOException {
    for (BaseTemplate node : decorators) {
      node.after(context, writer);
    }
  }

  @Override
  protected void merge(final Context context, final Writer writer)
      throws IOException {
    for (Template node : nodes) {
      node.apply(context, writer);
    }
  }

  @Override
  public final boolean decorate() {
    return decorate;
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
   * The number of children.
   *
   * @return The number of children.
   */
  public int size() {
    return nodes.size();
  }

  @Override
  public List<String> collect(final TagType... tagType) {
    Set<String> tagNames = new LinkedHashSet<String>();
    for (Template node : nodes) {
      tagNames.addAll(node.collect(tagType));
    }
    return new ArrayList<String>(tagNames);
  }

  @Override
  public List<String> collectReferenceParameters() {
    Set<String> paramNames = new LinkedHashSet<String>();
    for (Template node : nodes) {
      paramNames.addAll(node.collectReferenceParameters());
    }
    return new ArrayList<String>(paramNames);
  }

  @Override
  public String toString() {
    return nodes.toString();
  }
}
