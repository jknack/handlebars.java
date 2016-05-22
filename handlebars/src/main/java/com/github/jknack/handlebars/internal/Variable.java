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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Formatter;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Lambda;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.PathCompiler;
import com.github.jknack.handlebars.PathExpression;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

/**
 * The most basic tag type is the variable. A {{name}} tag in a basic template
 * will try to find the name key in the current context. If there is no name
 * key, nothing will be rendered.
 * All variables are HTML escaped by default. If you want to return unescaped
 * HTML, use the triple mustache: {{{name}}}.
 * You can also use & to unescape a variable: {{& name}}. This may be useful
 * when changing delimiters.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Variable extends HelperResolver {

  /**
   * The variable's name. Required.
   */
  protected final String name;

  /**
   * The variable type.
   */
  protected final TagType type;

  /**
   * The start delimiter.
   */
  private String startDelimiter;

  /**
   * The end delimiter.
   */
  private String endDelimiter;

  /**
   * The escaping strategy.
   */
  private EscapingStrategy escapingStrategy;

  /** Helper. */
  private Helper<Object> helper;

  /** Formatter. */
  private Formatter.Chain formatter;

  /** Missing value resolver. */
  private Helper<Object> missing;

  /** A compiled version of {@link #name}. */
  private List<PathExpression> path;

  /** Block params. */
  private static final List<String> BPARAMS = Collections.emptyList();

  /** True, when no param/hash. */
  private boolean noArg;

  /** Empty var. */
  private Template emptyVar;

  /**
   * Creates a new {@link Variable}.
   *
   * @param handlebars The handlebars instance.
   * @param name The variable's name. Required.
   * @param type The variable's type. Required.
   * @param params The variable's parameters. Required.
   * @param hash The variable's hash. Required.
   */
  public Variable(final Handlebars handlebars, final String name,
      final TagType type, final List<Param> params,
      final Map<String, Param> hash) {
    super(handlebars);
    this.name = name.trim();
    this.path = PathCompiler.compile(name, handlebars.parentScopeResolution());
    this.type = type;
    this.emptyVar = empty(this);
    params(params);
    hash(hash);
    this.escapingStrategy = type == TagType.VAR
        ? handlebars.getEscapingStrategy()
        : EscapingStrategy.NOOP;
    this.formatter = handlebars.getFormatter();
    this.noArg = params.size() == 0 && hash.size() == 0;
    postInit();
  }

  /**
   * Creates a new {@link Variable}.
   *
   * @param handlebars The handlebars instance.
   * @param name The variable's name. Required.
   * @param type The variable's type. Required.
   */
  @SuppressWarnings("unchecked")
  public Variable(final Handlebars handlebars, final String name, final TagType type) {
    this(handlebars, name, type, Collections.EMPTY_LIST,
        Collections.EMPTY_MAP);
  }

  /**
   * Apply any pending initialization.
   */
  protected void postInit() {
    this.helper = helper(name);
    this.missing = handlebars.helper(HelperRegistry.HELPER_MISSING);
  }

  /**
   * The variable's name.
   *
   * @return The variable's name.
   */
  public String name() {
    return name;
  }

  @Override
  protected void merge(final Context scope, final Writer writer)
      throws IOException {
    Object value = value(scope, writer);
    if (value != null) {
      writer.append(formatAndEscape(value, formatter));
    }
  }

  /**
   * Apply the template and return the raw value (not a CharSequence).
   *
   * @param scope Template scope.
   * @param writer Writer.
   * @return Resulting value.
   * @throws IOException If something goes wrong.
   */
  @SuppressWarnings("unchecked")
  public Object value(final Context scope, final Writer writer) throws IOException {
    boolean blockParam = scope.isBlockParams() && noArg;
    if (helper != null && !blockParam) {
      Options options = new Options(handlebars, name, type, scope, emptyVar, Template.EMPTY,
          params(scope), hash(scope), BPARAMS, writer);
      options.data(Context.PARAM_SIZE, this.params.size());
      return helper.apply(determineContext(scope), options);
    } else {
      Object value = scope.get(path);
      if (value == null) {
        if (missing != null) {
          Options options = new Options(handlebars, name, type, scope, emptyVar, Template.EMPTY,
              params(scope), hash(scope), BPARAMS, writer);
          options.data(Context.PARAM_SIZE, this.params.size());
          value = missing.apply(determineContext(scope), options);
        }
      }
      if (value instanceof Lambda) {
        value = Lambdas.merge(handlebars, (Lambda<Object, Object>) value, scope, this);
      }
      return value;
    }
  }

  /**
   * @param variable Source template.
   * @return An empty template.
   */
  private static Template empty(final Variable variable) {
    return new ForwardingTemplate(variable) {
      @Override
      public String apply(final Context context) throws IOException {
        return "";
      }

      @Override
      public void apply(final Context context, final Writer writer) throws IOException {
      }

      @Override
      public String apply(final Object context) throws IOException {
        return "";
      }

      @Override
      public void apply(final Object context, final Writer writer) throws IOException {
      }
    };
  }

  @Override
  protected void collect(final Collection<String> result, final TagType tagType) {
    if (this.type == tagType) {
      result.add(name);
    }
    super.collect(result, tagType);
  }

  /**
   * Format and escape a var (if need it).
   *
   * @param value The variable's value.
   * @param formatter Formatter to use.
   * @return Formatted and escaped value.
   */
  protected CharSequence formatAndEscape(final Object value, final Formatter.Chain formatter) {
    CharSequence formatted = formatter.format(value).toString();
    if (value instanceof Handlebars.SafeString) {
      return formatted;
    }
    return escapingStrategy.escape(formatted);
  }

  @Override
  public String text() {
    StringBuilder buffer = new StringBuilder();
    buffer.append(startDelimiter).append(suffix()).append(name);
    String params = paramsToString(this.params);
    if (params.length() > 0) {
      buffer.append(" ").append(params);
    }
    String hash = hashToString();
    if (hash.length() > 0) {
      buffer.append(" ").append(hash);
    }
    return buffer.append(endDelimiter).toString();
  }

  /**
   * @return Type suffix, default is empty.
   */
  protected String suffix() {
    return "";
  }

  /**
   * Set the end delimiter.
   *
   * @param endDelimiter The end delimiter.
   * @return This section.
   */
  public Variable endDelimiter(final String endDelimiter) {
    this.endDelimiter = endDelimiter;
    return this;
  }

  /**
   * Set the start delimiter.
   *
   * @param startDelimiter The start delimiter.
   * @return This section.
   */
  public Variable startDelimiter(final String startDelimiter) {
    this.startDelimiter = startDelimiter;
    return this;
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

}
