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

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Decorator;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Param;
import com.github.jknack.handlebars.PathCompiler;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.internal.HbsParser.AmpvarContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockParamsContext;
import com.github.jknack.handlebars.internal.HbsParser.BodyContext;
import com.github.jknack.handlebars.internal.HbsParser.BoolParamContext;
import com.github.jknack.handlebars.internal.HbsParser.CharParamContext;
import com.github.jknack.handlebars.internal.HbsParser.CommentContext;
import com.github.jknack.handlebars.internal.HbsParser.DynamicPathContext;
import com.github.jknack.handlebars.internal.HbsParser.ElseBlockContext;
import com.github.jknack.handlebars.internal.HbsParser.ElseStmtChainContext;
import com.github.jknack.handlebars.internal.HbsParser.ElseStmtContext;
import com.github.jknack.handlebars.internal.HbsParser.EscapeContext;
import com.github.jknack.handlebars.internal.HbsParser.HashContext;
import com.github.jknack.handlebars.internal.HbsParser.LiteralPathContext;
import com.github.jknack.handlebars.internal.HbsParser.ParamContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialBlockContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialContext;
import com.github.jknack.handlebars.internal.HbsParser.RawBlockContext;
import com.github.jknack.handlebars.internal.HbsParser.RefParamContext;
import com.github.jknack.handlebars.internal.HbsParser.SexprContext;
import com.github.jknack.handlebars.internal.HbsParser.StatementContext;
import com.github.jknack.handlebars.internal.HbsParser.StaticPathContext;
import com.github.jknack.handlebars.internal.HbsParser.StringParamContext;
import com.github.jknack.handlebars.internal.HbsParser.SubParamExprContext;
import com.github.jknack.handlebars.internal.HbsParser.TemplateContext;
import com.github.jknack.handlebars.internal.HbsParser.TextContext;
import com.github.jknack.handlebars.internal.HbsParser.TvarContext;
import com.github.jknack.handlebars.internal.HbsParser.UnlessContext;
import com.github.jknack.handlebars.internal.HbsParser.VarContext;
import com.github.jknack.handlebars.io.TemplateSource;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Traverse the parse tree and build templates.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
abstract class TemplateBuilder extends HbsParserBaseVisitor<Object> {

  /**
   * Get partial info: static vs dynamic.
   *
   * @author edgar
   * @since 2.2.0
   */
  private static class PartialInfo {

    /** Token to report errors. */
    private Token token;

    /** Partial params. */
    private Map<String, Param> hash;

    /** Partial path: static vs subexpression. */
    private Template path;

    /** Template context. */
    private String context;

  }

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
   * Keep track of the current text.
   */
  protected String currentText = null;

  /**
   * Keep track of block helpers.
   */
  private LinkedList<String> qualifier = new LinkedList<>();

  /**
   * Keep track of block helpers params.
   */
  private LinkedList<String> paramStack = new LinkedList<>();

  /** Keep track of block level, required for top level decorators. */
  private int level;

  /**
   * Creates a new {@link TemplateBuilder}.
   *
   * @param handlebars A handlebars object. required.
   * @param source The template source. required.
   */
  TemplateBuilder(final Handlebars handlebars, final TemplateSource source) {
    this.handlebars = notNull(handlebars, "The handlebars can't be null.");
    this.source = notNull(source, "The template source is required.");
  }

  @Override
  public Template visit(final ParseTree tree) {
    return (Template) super.visit(tree);
  }

  @Override
  public Template visitRawBlock(final RawBlockContext ctx) {
    level += 1;
    SexprContext sexpr = ctx.sexpr();
    Token nameStart = sexpr.QID().getSymbol();
    String name = nameStart.getText();
    qualifier.addLast(name);
    String nameEnd = ctx.nameEnd.getText();
    if (!name.equals(nameEnd)) {
      reportError(null, ctx.nameEnd.getLine(), ctx.nameEnd.getCharPositionInLine(),
          String.format("found: '%s', expected: '%s'", nameEnd, name));
    }

    hasTag(true);
    Block block = new Block(handlebars, name, false, "{{", params(sexpr.param()),
        hash(sexpr.hash()), Collections.emptyList());

    if (block.paramSize > 0) {
      paramStack.addLast(block.params.get(0).toString());
    }

    block.filename(source.filename());
    block.position(nameStart.getLine(), nameStart.getCharPositionInLine());
    String startDelim = ctx.start.getText();
    startDelim = startDelim.substring(0, startDelim.length() - 2);
    block.startDelimiter(startDelim);
    block.endDelimiter(ctx.stop.getText());

    Template body = visitBody(ctx.thenBody);
    if (body != null) {
      // rewrite raw template
      block.body(new Text(handlebars, body.text()));
    }
    hasTag(true);
    qualifier.removeLast();

    if (block.paramSize > 0) {
      paramStack.removeLast();
    }

    level -= 1;
    return block;
  }

  @Override
  public Template visitBlock(final BlockContext ctx) {
    level += 1;
    SexprContext sexpr = ctx.sexpr();
    boolean decorator = ctx.DECORATOR() != null;
    Token nameStart = sexpr.QID().getSymbol();
    String name = nameStart.getText();
    qualifier.addLast(name);
    String nameEnd = ctx.nameEnd.getText();
    if (!name.equals(nameEnd)) {
      reportError(null, ctx.nameEnd.getLine(), ctx.nameEnd.getCharPositionInLine(),
          String.format("found: '%s', expected: '%s'", nameEnd, name));
    }

    hasTag(true);
    Block block;
    if (decorator) {
      block = new BlockDecorator(handlebars, name, false, params(sexpr.param()),
          hash(sexpr.hash()), blockParams(ctx.blockParams()), level == 1);
    } else {
      block = new Block(handlebars, name, false, "#", params(sexpr.param()),
          hash(sexpr.hash()), blockParams(ctx.blockParams()));
    }
    if (block.paramSize > 0) {
      paramStack.addLast(block.params.get(0).toString());
    }
    block.filename(source.filename());
    block.position(nameStart.getLine(), nameStart.getCharPositionInLine());
    String startDelim = ctx.start.getText();
    String endDelim = ctx.stop.getText();
    startDelim = startDelim.substring(0, startDelim.length() - 1);
    block.startDelimiter(startDelim);
    block.endDelimiter(endDelim);

    Template body = visitBody(ctx.thenBody);
    if (body != null) {
      block.body(body);
    }
    // else
    Block elseroot = block;
    for (ElseBlockContext elseBlock : ctx.elseBlock()) {
      ElseStmtContext elseStmt = elseBlock.elseStmt();
      if (elseStmt != null) {
        // basic else
        Template unless = visitBody(elseStmt.unlessBody);
        if (unless != null) {
          String inverseLabel = elseStmt.inverseToken.getText();
          if (inverseLabel.startsWith(startDelim)) {
            inverseLabel = inverseLabel.substring(startDelim.length());
          }
          if (inverseLabel.endsWith("~")) {
            inverseLabel = inverseLabel.substring(0, inverseLabel.length() - 1);
          }
          elseroot.inverse(inverseLabel, unless);
        }
      } else {
        // else chain
        ElseStmtChainContext elseStmtChain = elseBlock.elseStmtChain();
        SexprContext elseexpr = elseStmtChain.sexpr();
        Token elsenameStart = elseexpr.QID().getSymbol();
        String elsename = elsenameStart.getText();
        String type = elseStmtChain.inverseToken.getText();
        if (type.equals("else")) {
          type = "else ";
        }
        Block elseblock = new Block(handlebars, elsename, false, type, params(elseexpr.param()),
            hash(elseexpr.hash()), blockParams(elseStmtChain.blockParams()));
        elseblock.filename(source.filename());
        elseblock.position(elsenameStart.getLine(), elsenameStart.getCharPositionInLine());
        elseblock.startDelimiter(startDelim);
        elseblock.endDelimiter(elseStmtChain.END().getText());
        Template elsebody = visitBody(elseStmtChain.unlessBody);
        elseblock.body(elsebody);

        String inverseLabel = elseStmtChain.inverseToken.getText();
        if (inverseLabel.startsWith(startDelim)) {
          inverseLabel = inverseLabel.substring(startDelim.length());
        }
        elseroot.inverse(inverseLabel, elseblock);
        elseroot = elseblock;
      }
    }
    hasTag(true);
    qualifier.removeLast();
    if (block.paramSize > 0) {
      paramStack.removeLast();
    }
    level -= 1;
    return block;
  }

  @Override
  public Template visitUnless(final UnlessContext ctx) {
    level += 1;
    hasTag(true);
    SexprContext sexpr = ctx.sexpr();
    Token nameStart = sexpr.QID().getSymbol();
    String name = nameStart.getText();
    qualifier.addLast(name);
    String nameEnd = ctx.nameEnd.getText();
    if (!name.equals(nameEnd)) {
      reportError(null, ctx.nameEnd.getLine(), ctx.nameEnd.getCharPositionInLine(),
          String.format("found: '%s', expected: '%s'", nameEnd, name));
    }
    Block block = new Block(handlebars, name, true, "^", Collections.emptyList(),
        Collections.emptyMap(), blockParams(ctx.blockParams()));
    block.filename(source.filename());
    block.position(nameStart.getLine(), nameStart.getCharPositionInLine());
    String startDelim = ctx.start.getText();
    block.startDelimiter(startDelim.substring(0, startDelim.length() - 1));
    block.endDelimiter(ctx.stop.getText());

    Template body = visitBody(ctx.body());
    if (body != null) {
      block.body(body);
    }
    hasTag(true);
    level -= 1;
    return block;
  }

  @Override
  public Template visitVar(final VarContext ctx) {
    hasTag(false);
    SexprContext sexpr = ctx.sexpr();
    return newVar(sexpr.QID().getSymbol(), TagType.VAR, params(sexpr.param()), hash(sexpr.hash()),
        ctx.start.getText(), ctx.stop.getText(), ctx.DECORATOR() != null);
  }

  @Override
  public Object visitEscape(final EscapeContext ctx) {
    Token token = ctx.ESC_VAR().getSymbol();
    String text = token.getText().substring(1);
    return new Text(handlebars, text, "\\")
        .filename(source.filename())
        .position(token.getLine(), token.getCharPositionInLine());
  }

  @Override
  public Template visitTvar(final TvarContext ctx) {
    hasTag(false);
    SexprContext sexpr = ctx.sexpr();
    return newVar(sexpr.QID().getSymbol(), TagType.TRIPLE_VAR, params(sexpr.param()),
        hash(sexpr.hash()),
        ctx.start.getText(), ctx.stop.getText(), false);
  }

  @Override
  public Template visitAmpvar(final AmpvarContext ctx) {
    hasTag(false);
    SexprContext sexpr = ctx.sexpr();
    return newVar(sexpr.QID().getSymbol(), TagType.AMP_VAR, params(sexpr.param()),
        hash(sexpr.hash()),
        ctx.start.getText(), ctx.stop.getText(), false);
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
   * @param decorator True, for var decorators.
   * @return A new {@link Variable}.
   */
  private Variable newVar(final Token name, final TagType varType, final List<Param> params,
      final Map<String, Param> hash, final String startDelimiter, final String endDelimiter,
      final boolean decorator) {
    String varName = name.getText();
    boolean isHelper = ((params.size() > 0 || hash.size() > 0)
        || varType == TagType.SUB_EXPRESSION);
    if (!isHelper && qualifier.size() > 0 && "with".equals(qualifier.getLast())
        && !varName.startsWith(".")) {
      // HACK to qualified 'with' in order to improve handlebars.js compatibility
      if (paramStack.size() > 0) {
        String scope = paramStack.getLast();
        if (varName.equals(scope) || varName.startsWith(scope + ".")) {
          varName = "this." + varName;
        }
      }
    }
    String[] parts = StringUtils.splitByWholeSeparator(varName, "./");
    // TODO: try to catch this with ANTLR...
    // foo.0 isn't allowed, it must be foo.0.
    if (parts.length > 0 && NumberUtils.isCreatable(parts[parts.length - 1])
        && !varName.endsWith(".")) {
      String evidence = varName;
      String reason = "found: " + varName + ", expecting: " + varName + ".";
      String message = source.filename() + ":" + name.getLine() + ":" + name.getChannel() + ": "
          + reason + "\n";
      throw new HandlebarsException(new HandlebarsError(source.filename(), name.getLine(),
          name.getCharPositionInLine(), reason, evidence, message));
    }
    if (decorator) {
      Decorator dec = handlebars.decorator(varName);
      if (dec == null) {
        reportError(null, name.getLine(), name.getCharPositionInLine(),
            "could not find decorator: '" + varName + "'");
      }
    } else {
      Helper<Object> helper = handlebars.helper(varName);
      if (helper == null && isHelper) {
        Helper<Object> helperMissing = handlebars.helper(HelperRegistry.HELPER_MISSING);
        if (helperMissing == null) {
          reportError(null, name.getLine(), name.getCharPositionInLine(), "could not find helper: '"
              + varName + "'");
        }
      }
    }
    Variable var = decorator
        ? new VarDecorator(handlebars, varName, TagType.STAR_VAR, params, hash, level == 0)
        : new Variable(handlebars, varName, varType, params, hash);
    var
        .startDelimiter(startDelimiter)
        .endDelimiter(endDelimiter)
        .filename(source.filename())
        .position(name.getLine(), name.getCharPositionInLine());
    return var;
  }

  /**
   * Build a hash.
   *
   * @param ctx The hash context.
   * @return A new hash.
   */
  private Map<String, Param> hash(final List<HashContext> ctx) {
    if (ctx == null || ctx.size() == 0) {
      return Collections.emptyMap();
    }
    Map<String, Param> result = new LinkedHashMap<>();
    for (HashContext hc : ctx) {
      result.put(hc.QID().getText(), (Param) super.visit(hc.param()));
    }
    return result;
  }

  /**
   * Build a hash.
   *
   * @param ctx The hash context.
   * @return A new hash.
   */
  private List<String> blockParams(final BlockParamsContext ctx) {
    if (ctx == null) {
      return Collections.emptyList();
    }
    List<TerminalNode> ids = ctx.QID();
    if (ids == null || ids.size() == 0) {
      return Collections.emptyList();
    }
    List<String> result = new ArrayList<>();
    for (TerminalNode id : ids) {
      result.add(id.getText());
    }
    return result;
  }

  /**
   * Build a param list.
   *
   * @param params The param context.
   * @return A new param list.
   */
  private List<Param> params(final List<ParamContext> params) {
    if (params == null || params.size() == 0) {
      return Collections.emptyList();
    }
    List<Param> result = new ArrayList<>();
    for (ParamContext param : params) {
      result.add((Param) super.visit(param));
    }
    return result;
  }

  @Override
  public Object visitBoolParam(final BoolParamContext ctx) {
    return new DefParam(Boolean.valueOf(ctx.getText()));
  }

  @Override
  public Object visitSubParamExpr(final SubParamExprContext ctx) {
    SexprContext sexpr = ctx.sexpr();
    return new VarParam(
        newVar(sexpr.QID().getSymbol(), TagType.SUB_EXPRESSION, params(sexpr.param()),
            hash(sexpr.hash()), "(", ")", false));
  }

  @Override
  public Object visitStringParam(final StringParamContext ctx) {
    return new StrParam(ctx.getText().replace("\\\"", "\""));
  }

  @Override
  public Object visitCharParam(final CharParamContext ctx) {
    return new StrParam(ctx.getText().replace("\\\'", "\'"));
  }

  @Override
  public Object visitRefParam(final RefParamContext ctx) {
    return new RefParam(PathCompiler.compile(ctx.getText(), handlebars.parentScopeResolution()));
  }

  @Override
  public Object visitNumberParam(final HbsParser.NumberParamContext ctx) {
    try {
      return new DefParam(Integer.parseInt(ctx.getText()));
    } catch (NumberFormatException x) {
      return new DefParam(Double.parseDouble(ctx.getText()));
    }
  }

  @Override
  public Template visitTemplate(final TemplateContext ctx) {
    Template template = visitBody(ctx.body());
    if (!handlebars.infiniteLoops() && template instanceof BaseTemplate) {
      template = infiniteLoop(source, (BaseTemplate) template);
    }
    destroy();
    return template;
  }

  /**
   * Get the last line of a string.
   *
   * @param str The string.
   * @return The last line.
   */
  private String getLastLine(final String str) {
    int i = StringUtils.lastIndexOfAny(str, "\r", "\n");
    if (i < 0) {
      return str;
    }
    return str.substring(i + 1);
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

    String indent;
    if (hasTag()) {
      indent = getLastLine(this.currentText);
      if (isEmpty(indent) || !isEmpty(indent.trim())) {
        indent = null;
      }
    } else {
      indent = null;
    }

    PartialInfo info = (PartialInfo) super.visit(ctx.pexpr());

    String startDelim = ctx.start.getText();
    Template partial = new Partial(handlebars, info.path, info.context, info.hash)
        .startDelimiter(startDelim.substring(0, startDelim.length() - 1))
        .endDelimiter(ctx.stop.getText())
        .indent(indent)
        .filename(source.filename())
        .position(info.token.getLine(), info.token.getCharPositionInLine());

    return partial;
  }

  @Override
  public Object visitPartialBlock(final PartialBlockContext ctx) {
    hasTag(true);

    String indent = this.currentText;
    if (hasTag()) {
      if (isEmpty(indent) || !isEmpty(indent.trim())) {
        indent = null;
      }
    } else {
      indent = null;
    }

    PartialInfo info = (PartialInfo) super.visit(ctx.pexpr());
    Template fn = visitBody(ctx.thenBody);

    String startDelim = ctx.start.getText();
    Template partial = new Partial(handlebars, info.path, info.context, info.hash)
        .setDecorate(true)
        .setPartial(fn)
        .startDelimiter(startDelim.substring(0, startDelim.length() - 1))
        .endDelimiter(ctx.stop.getText())
        .indent(indent)
        .filename(source.filename())
        .position(info.token.getLine(), info.token.getCharPositionInLine());

    return partial;
  }

  @Override
  public PartialInfo visitStaticPath(final StaticPathContext ctx) {
    return staticPath(ctx.path, ctx.QID(1), ctx.hash());
  }

  /**
   * Collect partial data.
   *
   * @param pathToken Path token.
   * @param partialContext Optional partial context.
   * @param hash Optional partial arguments.
   * @return Partial info.
   */
  private PartialInfo staticPath(final Token pathToken, final TerminalNode partialContext,
      final List<HashContext> hash) {
    String uri = pathToken.getText();
    if (uri.charAt(0) == '[' || uri.charAt(0) == '"' || uri.charAt(0) == '\'') {
      uri = uri.substring(1, uri.length() - 1);
    }

    if (uri.startsWith("/")) {
      String message = "found: '/', partial shouldn't start with '/'";
      reportError(null, pathToken.getLine(), pathToken.getCharPositionInLine(), message);
    }

    PartialInfo partial = new PartialInfo();
    partial.token = pathToken;
    partial.path = new Text(handlebars, uri);
    partial.hash = hash(hash);
    partial.context = partialContext != null ? partialContext.getText() : null;
    return partial;
  }

  @Override
  public PartialInfo visitLiteralPath(final LiteralPathContext ctx) {
    return staticPath(ctx.path, ctx.QID(), ctx.hash());
  }

  @Override
  public PartialInfo visitDynamicPath(final DynamicPathContext ctx) {
    SexprContext sexpr = ctx.sexpr();
    TerminalNode qid = sexpr.QID();
    Template expression = newVar(qid.getSymbol(), TagType.SUB_EXPRESSION, params(sexpr.param()),
        hash(sexpr.hash()), "(", ")", false);

    PartialInfo partial = new PartialInfo();
    partial.path = expression;
    partial.hash = hash(ctx.hash());
    TerminalNode scope = ctx.QID();
    partial.context = scope != null ? scope.getText() : null;
    partial.token = qid.getSymbol();
    return partial;
  }

  @Override
  public Template visitBody(final BodyContext ctx) {
    List<StatementContext> stats = ctx.statement();
    if (stats.size() == 0 || (stats.size() == 1 && stats.get(0) == Template.EMPTY)) {
      return Template.EMPTY;
    }

    TemplateList list = new TemplateList(handlebars);
    list.filename(source.filename());
    Template prev = null;
    boolean setMd = false;
    for (StatementContext statement : stats) {
      Template candidate = visit(statement);
      if (candidate != null) {
        if (!setMd) {
          list.filename(candidate.filename())
              .position(candidate.position()[0], candidate.position()[1]);
          setMd = true;
        }
        // join consecutive text
        if (candidate instanceof Text) {
          if (!(prev instanceof Text)) {
            list.add(candidate);
            prev = candidate;
          } else {
            ((Text) prev).append(((Text) candidate));
          }
        } else {
          list.add(candidate);
          prev = candidate;
        }
      }
    }

    return list;
  }

  @Override
  public Object visitComment(final CommentContext ctx) {
    return Template.EMPTY;
  }

  @Override
  public Template visitStatement(final StatementContext ctx) {
    return visit(ctx.getChild(0));
  }

  @Override
  public Template visitText(final TextContext ctx) {
    Token token = ctx.TEXT().getSymbol();
    String text = token.getText();
    this.currentText = text;

    if (token.getChannel() == Token.HIDDEN_CHANNEL) {
      return null;
    }

    if (token.getChannel() == MustacheSpec.REMOVE_LAST_LINE_CHANNEL) {
      String last = MustacheStringUtils.removeLastWhitespaceLine(text);
      text = last;
      this.hasTag = null;
    }

    return new Text(handlebars, text)
        .filename(source.filename())
        .position(ctx.start.getLine(), ctx.start.getCharPositionInLine());
  }

  /**
   * True, if tag instruction was processed.
   *
   * @return True, if tag instruction was processed.
   */
  private boolean hasTag() {
    if (handlebars.prettyPrint()) {
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
    this.currentText = null;
  }

  /**
   * Report a semantic error.
   *
   * @param offendingToken The offending token.
   * @param line The offending line.
   * @param column The offending column.
   * @param message An error message.
   */
  protected abstract void reportError(CommonToken offendingToken, int line,
      int column, String message);
}
