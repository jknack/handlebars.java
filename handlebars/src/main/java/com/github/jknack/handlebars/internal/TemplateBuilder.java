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
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.internal.HbsParser.AmpvarContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockContext;
import com.github.jknack.handlebars.internal.HbsParser.BodyContext;
import com.github.jknack.handlebars.internal.HbsParser.BoolHashContext;
import com.github.jknack.handlebars.internal.HbsParser.BoolParamContext;
import com.github.jknack.handlebars.internal.HbsParser.CharsHashContext;
import com.github.jknack.handlebars.internal.HbsParser.HashContext;
import com.github.jknack.handlebars.internal.HbsParser.IntHashContext;
import com.github.jknack.handlebars.internal.HbsParser.IntParamContext;
import com.github.jknack.handlebars.internal.HbsParser.NewlineContext;
import com.github.jknack.handlebars.internal.HbsParser.ParamContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialContext;
import com.github.jknack.handlebars.internal.HbsParser.RefHashContext;
import com.github.jknack.handlebars.internal.HbsParser.RefPramContext;
import com.github.jknack.handlebars.internal.HbsParser.SpacesContext;
import com.github.jknack.handlebars.internal.HbsParser.StatementContext;
import com.github.jknack.handlebars.internal.HbsParser.StringHashContext;
import com.github.jknack.handlebars.internal.HbsParser.StringParamContext;
import com.github.jknack.handlebars.internal.HbsParser.TemplateContext;
import com.github.jknack.handlebars.internal.HbsParser.TextContext;
import com.github.jknack.handlebars.internal.HbsParser.TvarContext;
import com.github.jknack.handlebars.internal.HbsParser.UnlessContext;
import com.github.jknack.handlebars.internal.HbsParser.VarContext;
import com.github.jknack.handlebars.internal.Variable.Type;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Traverse the parse tree and build templates.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
abstract class TemplateBuilder extends HbsParserBaseVisitor<Object> {

  /**
   * A handlebars object. required.
   */
  private Handlebars handlebars;

  /**
   * The template source. Required.
   */
  private TemplateSource source;

  /**
   * Flag to track dead spaces and lines.
   */
  private Boolean hasTag;

  /**
   * Keep track of the current line.
   */
  protected StringBuilder line = new StringBuilder();

  /**
   * Creates a new {@link TemplateBuilder}.
   *
   * @param handlebars A handlbars object. required.
   * @param source The template source. required.
   */
  public TemplateBuilder(final Handlebars handlebars, final TemplateSource source) {
    this.handlebars = notNull(handlebars, "The handlebars can't be null.");
    this.source = notNull(source, "The template source is requied.");
  }

  @Override
  public Template visit(final ParseTree tree) {
    return (Template) super.visit(tree);
  }

  @Override
  public Template visitBlock(final BlockContext ctx) {
    String nameStart = ctx.nameStart.getText();
    String nameEnd = ctx.nameEnd.getText();
    if (!nameStart.equals(nameEnd)) {
      reportError(null, ctx.nameEnd.getLine(), ctx.nameEnd.getCharPositionInLine()
          , String.format("found: '%s', expected: '%s'", nameEnd, nameStart));
    }

    hasTag(true);
    Block block = new Block(handlebars, nameStart, false, params(ctx.param()),
        hash(ctx.hash()));
    block.filename(source.filename());
    block.position(ctx.nameStart.getLine(), ctx.nameStart.getCharPositionInLine());
    String startDelim = ctx.start.getText();
    block.startDelimiter(startDelim.substring(0, startDelim.length() - 1));
    block.endDelimiter(ctx.stop.getText());

    Template body = visitBody(ctx.thenBody);
    if (body != null) {
      block.body(body);
    }
    if (ctx.unlessBody != null) {
      Template unless = visitBody(ctx.unlessBody);
      if (unless != null) {
        block.inverse(unless);
      }
    }
    hasTag(true);
    return block;
  }

  @Override
  public Template visitUnless(final UnlessContext ctx) {
    hasTag(true);
    Block block = new Block(handlebars, ctx.nameStart.getText(), true, Collections.emptyList(),
        Collections.<String, Object> emptyMap());
    block.filename(source.filename());
    block.position(ctx.nameStart.getLine(), ctx.nameStart.getCharPositionInLine());
    String startDelim = ctx.start.getText();
    block.startDelimiter(startDelim.substring(0, startDelim.length() - 1));
    block.endDelimiter(ctx.stop.getText());

    Template body = visitBody(ctx.body());
    if (body != null) {
      block.body(body);
    }
    hasTag(true);
    return block;
  }

  @Override
  public Template visitVar(final VarContext ctx) {
    hasTag(false);
    return newVar(ctx.QID().getSymbol(), Type.VAR, params(ctx.param()), hash(ctx.hash()),
        ctx.start.getText(), ctx.stop.getText());
  }

  @Override
  public Template visitTvar(final TvarContext ctx) {
    hasTag(false);
    return newVar(ctx.QID().getSymbol(), Type.TRIPLE_VAR, params(ctx.param()), hash(ctx.hash()),
        ctx.start.getText(), ctx.stop.getText());
  }

  @Override
  public Template visitAmpvar(final AmpvarContext ctx) {
    hasTag(false);
    return newVar(ctx.QID().getSymbol(), Type.AMPERSAND_VAR, params(ctx.param()), hash(ctx.hash()),
        ctx.start.getText(), ctx.stop.getText());
  }

  /**
   * Build a new {@link Variable}.
   *
   * @param name The var's name.
   * @param varType The var's type.
   * @param params The var params.
   * @param hash The var hash.
   * @param startDelimiter The current start delimiter.
   * @param endDelimiter The current end delimiter.
   * @return A new {@link Variable}.
   */
  private Template newVar(final Token name, final Type varType, final List<Object> params,
      final Map<String, Object> hash, final String startDelimiter, final String endDelimiter) {
    String varName = name.getText();
    Helper<Object> helper = handlebars.helper(varName);
    if (helper == null && (params.size() > 0 || hash.size() > 0)) {
      Helper<Object> helperMissing =
          handlebars.helper(Handlebars.HELPER_MISSING);
      if (helperMissing == null) {
        reportError(null, name.getLine(), name.getCharPositionInLine(), "could not find helper: '"
            + varName + "'");
      }
    }
    return new Variable(handlebars, varName, varType, params, hash)
        .startDelimiter(startDelimiter)
        .endDelimiter(endDelimiter)
        .filename(source.filename())
        .position(name.getLine(), name.getCharPositionInLine());
  }

  /**
   * Build a hash.
   *
   * @param ctx The hash context.
   * @return A new hash.
   */
  private Map<String, Object> hash(final List<HashContext> ctx) {
    if (ctx == null || ctx.size() == 0) {
      return Collections.emptyMap();
    }
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    for (HashContext hc : ctx) {
      result.put(hc.QID().getText(), super.visit(hc.hashValue()));
    }
    return result;
  }

  /**
   * Build a param list.
   *
   * @param params The param context.
   * @return A new param list.
   */
  private List<Object> params(final List<ParamContext> params) {
    if (params == null || params.size() == 0) {
      return Collections.emptyList();
    }
    List<Object> result = new ArrayList<Object>();
    for (ParamContext param : params) {
      result.add(super.visit(param));
    }
    return result;
  }

  @Override
  public Object visitBoolParam(final BoolParamContext ctx) {
    return Boolean.valueOf(ctx.getText());
  }

  @Override
  public Object visitBoolHash(final BoolHashContext ctx) {
    return Boolean.valueOf(ctx.getText());
  }

  @Override
  public Object visitCharsHash(final CharsHashContext ctx) {
    return ctx.getText().replace("\\\'", "\'");
  }

  @Override
  public Object visitStringHash(final StringHashContext ctx) {
    return ctx.getText().replace("\\\"", "\"");
  }

  @Override
  public Object visitStringParam(final StringParamContext ctx) {
    return ctx.getText().replace("\\\"", "\"");
  }

  @Override
  public Object visitRefHash(final RefHashContext ctx) {
    return ctx.getText();
  }

  @Override
  public Object visitRefPram(final RefPramContext ctx) {
    return ctx.getText();
  }

  @Override
  public Object visitIntHash(final IntHashContext ctx) {
    return Integer.parseInt(ctx.getText());
  }

  @Override
  public Object visitIntParam(final IntParamContext ctx) {
    return Integer.parseInt(ctx.getText());
  }

  @Override
  public Template visitTemplate(final TemplateContext ctx) {
    Template template = visitBody(ctx.body());
    if (!handlebars.allowInfiniteLoops() && template instanceof BaseTemplate) {
      template = infiniteLoop(source, (BaseTemplate) template);
    }
    destroy();
    return template;
  }

  /**
   * Creates a {@link Template} that detects recursively calls.
   *
   * @param source The template source.
   * @param template The original template.
   * @return A new {@link Template} that detects recursively calls.
   */
  private static Template infiniteLoop(final TemplateSource source, final BaseTemplate template) {
    return new ForwardingTemplate(template) {
      @Override
      protected void beforeApply(final Context context) {
        LinkedList<TemplateSource> invocationStack = context.data(Context.INVOCATION_STACK);
        invocationStack.addLast(source);
      }

      @Override
      protected void afterApply(final Context context) {
        LinkedList<TemplateSource> invocationStack = context.data(Context.INVOCATION_STACK);
        if (!invocationStack.isEmpty()) {
          invocationStack.removeLast();
        }
      }
    };
  }

  @Override
  public Template visitPartial(final PartialContext ctx) {
    hasTag(true);
    Token pathToken = ctx.PATH().getSymbol();
    String uri = pathToken.getText();
    if (uri.startsWith("[") && uri.endsWith("]")) {
      uri = uri.substring(1, uri.length() - 1);
    }

    if (uri.startsWith("/")) {
      String message = "found: '/', partial shouldn't start with '/'";
      reportError(null, pathToken.getLine(), pathToken.getCharPositionInLine(), message);
    }

    String indent = line.toString();
    if (hasTag()) {
      if (isEmpty(indent) || !isEmpty(indent.trim())) {
        indent = null;
      }
    } else {
      indent = null;
    }

    TerminalNode partialContext = ctx.QID();
    String startDelim = ctx.start.getText();
    Template partial = new Partial(handlebars, uri,
        partialContext != null ? partialContext.getText() : "this")
        .startDelimiter(startDelim.substring(0, startDelim.length() - 1))
        .endDelimiter(ctx.stop.getText())
        .indent(indent)
        .filename(source.filename())
        .position(pathToken.getLine(), pathToken.getCharPositionInLine());

    return partial;
  }

  @Override
  public Template visitBody(final BodyContext ctx) {
    List<StatementContext> stats = ctx.statement();
    if (stats.size() == 0) {
      return Template.EMPTY;
    }
    if (stats.size() == 1) {
      return visit(stats.get(0));
    }
    TemplateList list = new TemplateList();
    Template prev = null;
    for (StatementContext statement : stats) {
      Template candidate = visit(statement);
      if (candidate != null) {
        // join consecutive piece of text
        if (candidate instanceof Text) {
          if (!(prev instanceof Text)) {
            list.add(candidate);
            prev = candidate;
          } else {
            ((Text) prev).append(((Text) candidate).text());
          }
        } else {
          list.add(candidate);
          prev = candidate;
        }
      }
    }
    if (list.size() == 1) {
      return list.iterator().next();
    }
    return list;
  }

  @Override
  public Template visitStatement(final StatementContext ctx) {
    return visit(ctx.getChild(0));
  }

  @Override
  public Template visitText(final TextContext ctx) {
    String text = ctx.getText();
    line.append(text);
    return new Text(text);
  }

  @Override
  public Template visitSpaces(final SpacesContext ctx) {
    Token space = ctx.SPACE().getSymbol();
    String text = space.getText();
    line.append(text);
    if (space.getChannel() == Token.HIDDEN_CHANNEL) {
      return null;
    }
    return new Text(text);
  }

  @Override
  public BaseTemplate visitNewline(final NewlineContext ctx) {
    Token newline = ctx.NL().getSymbol();
    if (newline.getChannel() == Token.HIDDEN_CHANNEL) {
      return null;
    }
    line.setLength(0);
    return new Text(newline.getText());
  }

  /**
   * True, if tag instruction was processed.
   *
   * @return True, if tag instruction was processed.
   */
  private boolean hasTag() {
    if (handlebars.prettyWhitespaces()) {
      return hasTag == null ? false : hasTag.booleanValue();
    }
    return false;
  }

  /**
   * Set if a new tag instruction was processed.
   *
   * @param hasTag True, if a new tag instruction was processed.
   */
  private void hasTag(final boolean hasTag) {
    if (this.hasTag != Boolean.FALSE) {
      this.hasTag = hasTag;
    }
  }

  /**
   * Cleanup resources.
   */
  private void destroy() {
    this.handlebars = null;
    this.source = null;
    this.hasTag = null;
    this.line.delete(0, line.length());
    this.line = null;
  }

  /**
   * Report a semantic error.
   *
   * @param offendingToken The offending token.
   * @param message An error message.
   */
  protected void reportError(final CommonToken offendingToken, final String message) {
    reportError(offendingToken, offendingToken.getLine(), offendingToken.getCharPositionInLine(),
        message);
  }

  /**
   * Report a semantic error.
   *
   * @param offendingToken The offending token.
   * @param line The offending line.
   * @param column The offending column.
   * @param message An error message.
   */
  protected abstract void reportError(final CommonToken offendingToken, final int line,
      final int column, final String message);
}
