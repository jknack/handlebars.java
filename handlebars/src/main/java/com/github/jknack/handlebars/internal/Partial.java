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

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Partials begin with a greater than sign, like {{> box}}.
 * Partials are rendered at runtime (as opposed to compile time), so recursive
 * partials are possible. Just avoid infinite loops.
 * They also inherit the calling context.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Partial extends HelperResolver {

  /**
   * The partial path.
   */
  private Template path;

  /**
   * A partial context. Optional.
   */
  private String context;

  /** Null-Safe version of {@link #context}. */
  private String scontext;

  /**
   * The start delimiter.
   */
  private String startDelimiter;

  /**
   * The end delimiter.
   */
  private String endDelimiter;

  /**
   * The indent to apply to the partial.
   */
  private String indent;

  /** Template loader. */
  private TemplateLoader loader;

  /** A partial block body. */
  private Template partial;

  /**
   * Creates a new {@link Partial}.
   *
   * @param handlebars The Handlebars object. Required.
   * @param path The template path.
   * @param context The template context.
   * @param hash Template params
   */
  public Partial(final Handlebars handlebars, final Template path, final String context,
      final Map<String, Param> hash) {
    super(handlebars);
    this.path = notNull(path, "The path is required.");
    this.context = context;
    this.scontext = context == null ? "this" : context;
    this.hash(hash);
    this.loader = handlebars.getLoader();
  }

  @Override
  public void before(final Context context, final Writer writer) throws IOException {
    LinkedList<Map<String, Template>> partials = context.data(Context.INLINE_PARTIALS);
    partials.addLast(new HashMap<String, Template>(partials.getLast()));
  }

  @Override
  public void after(final Context context, final Writer writer) throws IOException {
    LinkedList<Map<String, Template>> partials = context.data(Context.INLINE_PARTIALS);
    partials.removeLast();
  }

  @Override
  protected void merge(final Context context, final Writer writer)
      throws IOException {
    try {
      String path = this.path.apply(context);
      /** Inline partial? */
      LinkedList<Map<String, Template>> partials = context.data(Context.INLINE_PARTIALS);
      Map<String, Template> inlineTemplates = partials.getLast();

      if (this.partial != null) {
        this.partial.apply(context);
        inlineTemplates.put("@partial-block", this.partial);
      }

      Template template = inlineTemplates.get(path);

      if (template == null) {
        LinkedList<TemplateSource> invocationStack = context.data(Context.INVOCATION_STACK);

        try {
          TemplateSource source = loader.sourceAt(path);

          if (exists(invocationStack, source.filename())) {
            TemplateSource caller = invocationStack.removeLast();
            Collections.reverse(invocationStack);

            final String message;
            final String reason;
            if (invocationStack.isEmpty()) {
              reason = String.format("infinite loop detected, partial '%s' is calling itself",
                  source.filename());

              message = String.format("%s:%s:%s: %s", caller.filename(), line, column, reason);
            } else {
              reason = String.format(
                  "infinite loop detected, partial '%s' was previously loaded", source.filename());

              message = String.format("%s:%s:%s: %s\n%s", caller.filename(), line, column, reason,
                  "at " + join(invocationStack, "\nat "));
            }
            HandlebarsError error = new HandlebarsError(caller.filename(), line,
                column, reason, text(), message);
            throw new HandlebarsException(error);
          }

          if (indent != null) {
            source = partial(source, indent);
          }

          template = handlebars.compile(source);
        } catch (FileNotFoundException fnf) {
          if (this.partial != null) {
            template = this.partial;
          } else {
            throw fnf;
          }
        }

      }
      Context ctx = Context.newPartialContext(context, this.scontext, hash(context));
      template.apply(ctx, writer);
    } catch (IOException ex) {
      String reason = String.format("The partial '%s' at '%s' could not be found",
          loader.resolve(path.text()), ex.getMessage());
      String message = String.format("%s:%s:%s: %s", filename, line, column, reason);
      HandlebarsError error = new HandlebarsError(filename, line,
          column, reason, text(), message);
      throw new HandlebarsException(error);
    }
  }

  /**
   * True, if the file was already processed.
   *
   * @param invocationStack The current invocation stack.
   * @param filename The filename to check for.
   * @return True, if the file was already processed.
   */
  private static boolean exists(final List<TemplateSource> invocationStack,
      final String filename) {
    for (TemplateSource ts : invocationStack) {
      if (ts.filename().equals(filename)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Custom template source that insert an indent per each new line found. This is required by
   * Mustache Spec.
   *
   * @param source The original template source.
   * @param indent The partial indent.
   * @return A template source that insert an indent per each new line found. This is required by
   *         Mustache Spec.
   */
  private static TemplateSource partial(final TemplateSource source, final String indent) {
    return new TemplateSource() {
      @Override
      public long lastModified() {
        return source.lastModified();
      }

      @Override
      public String filename() {
        return source.filename();
      }

      @Override
      public String content() throws IOException {
        return partialInput(source.content(), indent);
      }

      @Override
      public int hashCode() {
        return source.hashCode();
      }

      @Override
      public boolean equals(final Object obj) {
        return source.equals(obj);
      }

      @Override
      public String toString() {
        return source.toString();
      }

      /**
       * Apply the given indent to the start of each line if necessary.
       *
       * @param input The whole input.
       * @param indent The indent to apply.
       * @return A new input.
       */
      private String partialInput(final String input, final String indent) {
        StringBuilder buffer = new StringBuilder(input.length() + indent.length());
        buffer.append(indent);
        int len = input.length();
        for (int idx = 0; idx < len; idx++) {
          char ch = input.charAt(idx);
          buffer.append(ch);
          if (ch == '\n' && idx < len - 1) {
            buffer.append(indent);
          }
        }
        return buffer.toString();
      }
    };
  }

  @Override
  public String text() {
    String path = this.path.text();
    StringBuilder buffer = new StringBuilder(startDelimiter)
        .append('>')
        .append(path);

    if (context != null) {
      buffer.append(' ').append(context);
    }

    buffer.append(endDelimiter);

    if (this.partial != null) {
      buffer.append(this.partial.text()).append(startDelimiter, 0, startDelimiter.length() - 1)
          .append("/").append(path).append(endDelimiter);
    }
    return buffer.toString();
  }

  /**
   * Set the end delimiter.
   *
   * @param endDelimiter The end delimiter.
   * @return This section.
   */
  public Partial endDelimiter(final String endDelimiter) {
    this.endDelimiter = endDelimiter;
    return this;
  }

  /**
   * Set the start delimiter.
   *
   * @param startDelimiter The start delimiter.
   * @return This section.
   */
  public Partial startDelimiter(final String startDelimiter) {
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

  /**
   * Set an indent for the partial.
   *
   * @param indent The indent.
   * @return This partial.
   */
  public Partial indent(final String indent) {
    this.indent = indent;
    return this;
  }

  /**
   * Set a partial block body.
   *
   * @param fn Partial block.
   * @return This partial.
   */
  public Partial setPartial(final Template fn) {
    this.partial = fn;
    return this;
  }

}
