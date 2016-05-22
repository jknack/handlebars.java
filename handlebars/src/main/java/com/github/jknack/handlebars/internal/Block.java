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

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Lambda;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.PathCompiler;
import com.github.jknack.handlebars.PathExpression;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.EachHelper;
import com.github.jknack.handlebars.helper.IfHelper;
import com.github.jknack.handlebars.helper.UnlessHelper;
import com.github.jknack.handlebars.helper.WithHelper;

/**
 * Blocks render blocks of text one or more times, depending on the value of
 * the key in the current context.
 * A section begins with a pound and ends with a slash. That is, {{#person}}
 * begins a "person" section while {{/person}} ends it.
 * The behavior of the block is determined by the value of the key if the block
 * isn't present.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Block extends HelperResolver {

  /**
   * The body template.
   */
  protected Template body;

  /**
   * The section's name.
   */
  protected final String name;

  /**
   * True if it's inverted.
   */
  private final boolean inverted;

  /**
   * Section's description '#' or '^'.
   */
  private final String type;

  /**
   * The start delimiter.
   */
  private String startDelimiter;

  /**
   * The end delimiter.
   */
  private String endDelimiter;

  /**
   * Inverse section for if/else clauses.
   */
  protected Template inverse = Template.EMPTY;

  /**
   * The inverse label: 'else' or '^'.
   */
  private String inverseLabel;

  /** Helper. */
  protected Helper<Object> helper;

  /** Block param names. */
  protected final List<String> blockParams;

  /** Tag type, default: is {@link TagType#SECTION}. */
  protected TagType tagType;

  /** Compiled path for {@link #name()}. */
  protected final List<PathExpression> path;

  /** True, if this block has decorators. */
  private boolean decorate;

  /**
   * Creates a new {@link Block}.
   *
   * @param handlebars The handlebars object.
   * @param name The section's name.
   * @param inverted True if it's inverted.
   * @param type Block type: <code>#</code>, <code>^</code>, <code>#*</code>, <code>{{</code>.
   * @param params The parameter list.
   * @param hash The hash.
   * @param blockParams The block param names.
   */
  public Block(final Handlebars handlebars, final String name,
      final boolean inverted, final String type, final List<Param> params,
      final Map<String, Param> hash, final List<String> blockParams) {
    super(handlebars);
    this.name = notNull(name, "The name is required.");
    this.path = PathCompiler.compile(name, handlebars.parentScopeResolution());
    this.inverted = inverted;
    this.type = type;
    params(params);
    hash(hash);
    this.blockParams = blockParams;
    this.tagType = TagType.SECTION;
    postInit();
  }

  /**
   * Make/run any pending or required initialization.
   */
  protected void postInit() {
    this.helper = helper(name);
  }

  @Override
  public void before(final Context context, final Writer writer) throws IOException {
    if (body instanceof BaseTemplate) {
      LinkedList<Map<String, Template>> partials = context.data(Context.INLINE_PARTIALS);
      partials.addLast(new HashMap<>(partials.getLast()));
      ((BaseTemplate) body).before(context, writer);
    }
  }

  @Override
  public void after(final Context context, final Writer writer) throws IOException {
    if (body instanceof BaseTemplate) {
      ((BaseTemplate) body).after(context, writer);
      LinkedList<Map<String, Template>> partials = context.data(Context.INLINE_PARTIALS);
      partials.removeLast();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void merge(final Context context, final Writer writer) throws IOException {
    final String helperName;
    Template template = body;
    final Object it;
    Context itCtx = context;
    Helper<Object> helper = this.helper;
    if (helper == null) {
      it = transform(itCtx.get(this.path));
      if (inverted) {
        helperName = UnlessHelper.NAME;
      } else if (it instanceof Iterable) {
        helperName = EachHelper.NAME;
      } else if (it instanceof Boolean) {
        helperName = IfHelper.NAME;
      } else if (it instanceof Lambda) {
        helperName = WithHelper.NAME;
        template = Lambdas
            .compile(handlebars, (Lambda<Object, Object>) it, context, template,
                startDelimiter, endDelimiter);
      } else {
        helperName = WithHelper.NAME;
        itCtx = Context.newContext(context, it);
      }
      // A built-in helper might be override it.
      helper = handlebars.helper(helperName);

      if (it == null) {
        Helper<Object> missing = helper(Handlebars.HELPER_MISSING);
        if (missing != null) {
          // use missing here
          helper = missing;
        }
      }
    } else {
      helperName = name;
      it = transform(determineContext(context));
    }

    Options options = new Options(handlebars, helperName, tagType, itCtx, template, inverse,
        params(itCtx), hash(itCtx), blockParams, writer);
    options.data(Context.PARAM_SIZE, this.params.size());

    Object result = helper.apply(it, options);
    if (result != null) {
      writer.append(result.toString());
    }
  }

  /**
   * The section's name.
   *
   * @return The section's name.
   */
  public String name() {
    return name;
  }

  /**
   * True if it's an inverted section.
   *
   * @return True if it's an inverted section.
   */
  public boolean inverted() {
    return inverted;
  }

  /**
   * Set the template body.
   *
   * @param body The template body. Required.
   * @return This section.
   */
  public Block body(final Template body) {
    this.body = notNull(body, "The template's body is required.");
    if (body instanceof BaseTemplate) {
      decorate = ((BaseTemplate) body).decorate();
    }
    return this;
  }

  @Override
  public boolean decorate() {
    return decorate;
  }

  /**
   * Set the inverse template.
   *
   * @param inverseLabel One of 'else' or '^'. Required.
   * @param inverse The inverse template. Required.
   * @return This section.
   */
  public Template inverse(final String inverseLabel, final Template inverse) {
    notNull(inverseLabel, "The inverseLabel can't be null.");
    isTrue(inverseLabel.equals("^") || inverseLabel.equals("else"),
        "The inverseLabel must be one of '^' or 'else'.");
    this.inverseLabel = inverseLabel;
    this.inverse = notNull(inverse, "The inverse's template is required.");
    return this;
  }

  /**
   * The inverse template for else clauses.
   *
   * @return The inverse template for else clauses.
   */
  public Template inverse() {
    return inverse;
  }

  /**
   * Set the end delimiter.
   *
   * @param endDelimiter The end delimiter.
   * @return This section.
   */
  public Block endDelimiter(final String endDelimiter) {
    this.endDelimiter = endDelimiter;
    return this;
  }

  /**
   * Set the start delimiter.
   *
   * @param startDelimiter The start delimiter.
   * @return This section.
   */
  public Block startDelimiter(final String startDelimiter) {
    this.startDelimiter = startDelimiter;
    return this;
  }

  /**
   * The template's body.
   *
   * @return The template's body.
   */
  public Template body() {
    return body;
  }

  @Override
  public String text() {
    return text(true);
  }

  /**
   * Build a text version of this block.
   *
   * @param complete True if the inner block should be added.
   * @return A string version of this block.
   */
  private String text(final boolean complete) {
    StringBuilder buffer = new StringBuilder();
    buffer.append(startDelimiter).append(type).append(name);
    String params = paramsToString(this.params);
    if (params.length() > 0) {
      buffer.append(" ").append(params);
    }
    String hash = hashToString();
    if (hash.length() > 0) {
      buffer.append(" ").append(hash);
    }
    if (blockParams.size() > 0) {
      buffer.append(" as |").append(paramsToString(this.blockParams)).append("|");
    }
    buffer.append(endDelimiter);
    if (complete) {
      buffer.append(body == null ? "" : body.text());
      buffer.append(inverse == Template.EMPTY ? "" : "{{" + inverseLabel + "}}" + inverse.text());
    } else {
      buffer.append("\n...\n");
    }
    buffer.append(startDelimiter);
    if (type.equals("{{")) {
      buffer.append("{{");
    }
    buffer.append('/').append(name).append(endDelimiter);
    return buffer.toString();
  }

  /**
   * The start delimiter.
   *
   * @return The start delimiter.
   */
  public String startDelimiter() {
    return startDelimiter;
  }

  /**
   * The end delimiter.
   *
   * @return The end delimiter.
   */
  public String endDelimiter() {
    return endDelimiter;
  }

  @Override
  public List<String> collect(final TagType... tagType) {
    Set<String> tagNames = new LinkedHashSet<String>();
    if (body != null) {
      tagNames.addAll(body.collect(tagType));
    }
    tagNames.addAll(inverse.collect(tagType));
    tagNames.addAll(super.collect(tagType));
    return new ArrayList<String>(tagNames);
  }

  @Override
  protected void collect(final Collection<String> result, final TagType tagType) {
    if (tagType == this.tagType) {
      result.add(name);
    }
    super.collect(result, tagType);
  }

  @Override
  public List<String> collectReferenceParameters() {
    Set<String> paramNames = new LinkedHashSet<String>();
    if (body != null) {
      paramNames.addAll(body.collectReferenceParameters());
    }
    paramNames.addAll(inverse.collectReferenceParameters());
    paramNames.addAll(super.collectReferenceParameters());
    return new ArrayList<String>(paramNames);
  }
}
