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

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Lambda;
import com.github.jknack.handlebars.Options;
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
  private Template body;

  /**
   * The section's name.
   */
  private final String name;

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
  private Template inverse;

  /**
   * The inverse label: 'else' or '^'.
   */
  private String inverseLabel;

  /**
   * Creates a new {@link Block}.
   *
   * @param handlebars The handlebars object.
   * @param name The section's name.
   * @param inverted True if it's inverted.
   * @param params The parameter list.
   * @param hash The hash.
   */
  public Block(final Handlebars handlebars, final String name,
      final boolean inverted, final List<Object> params,
      final Map<String, Object> hash) {
    super(handlebars);
    this.name = notNull(name, "The name is required.");
    this.inverted = inverted;
    type = inverted ? "^" : "#";
    params(params);
    hash(hash);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void merge(final Context context, final Writer writer) throws IOException {
    if (body == null) {
      return;
    }
    final String helperName;
    Helper<Object> helper = helper(name);
    Template template = body;
    final Object childContext;
    Context currentScope = context;
    if (helper == null) {
      childContext = transform(context.get(name));
      if (inverted) {
        helperName = UnlessHelper.NAME;
      } else if (childContext instanceof Iterable) {
        helperName = EachHelper.NAME;
      } else if (childContext instanceof Boolean) {
        helperName = IfHelper.NAME;
      } else if (childContext instanceof Lambda) {
        helperName = WithHelper.NAME;
        template = Lambdas
            .compile(handlebars,
                (Lambda<Object, Object>) childContext,
                context, template,
                startDelimiter, endDelimiter);
      } else {
        helperName = WithHelper.NAME;
        currentScope = Context.newContext(context, childContext);
      }
      // A built-in helper might be override it.
      helper = handlebars.helper(helperName);
    } else {
      helperName = name;
      childContext = transform(determineContext(context));
    }
    Options options = new Options.Builder(handlebars, helperName, TagType.SECTION, currentScope,
        template)
        .setInverse(inverse == null ? Template.EMPTY : inverse)
        .setParams(params(currentScope))
        .setHash(hash(context))
        .build();
    options.data(Context.PARAM_SIZE, this.params.size());

    CharSequence result = helper.apply(childContext, options);
    if (!isEmpty(result)) {
      writer.append(result);
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
    return this;
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
    String params = paramsToString();
    if (params.length() > 0) {
      buffer.append(" ").append(params);
    }
    String hash = hashToString();
    if (hash.length() > 0) {
      buffer.append(" ").append(hash);
    }
    buffer.append(endDelimiter);
    if (complete) {
      buffer.append(body == null ? "" : body.text());
      buffer.append(inverse == null ? "" : "{{" + inverseLabel + "}}" + inverse.text());
    } else {
      buffer.append("\n...\n");
    }
    buffer.append(startDelimiter).append('/').append(name).append(endDelimiter);
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
    if (inverse != null) {
      tagNames.addAll(inverse.collect(tagType));
    }
    tagNames.addAll(super.collect(tagType));
    return new ArrayList<String>(tagNames);
  }

  @Override
  protected void collect(final Collection<String> result, final TagType tagType) {
    if (tagType == TagType.SECTION) {
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
    if (inverse != null) {
      paramNames.addAll(inverse.collectReferenceParameters());
    }
    paramNames.addAll(super.collectReferenceParameters());
    return new ArrayList<String>(paramNames);
  }

  @Override
  protected void collectReferenceParameters(final Collection<String> result) {
    for (Object param : params) {
      if (ParamType.REFERENCE.apply(param) && !ParamType.STRING.apply(param)) {
        result.add((String) param);
      }
    }
    for (Object hashValue : hash.values()) {
      if (ParamType.REFERENCE.apply(hashValue) && !ParamType.STRING.apply(hashValue)) {
        result.add((String) hashValue);
      }
    }
    super.collectReferenceParameters(result);
  }
}
