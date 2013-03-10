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

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TemplateLoader;
import com.github.jknack.handlebars.internal.HbsParser.AmpvarContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockContext;
import com.github.jknack.handlebars.internal.HbsParser.BodyContext;
import com.github.jknack.handlebars.internal.HbsParser.HashContext;
import com.github.jknack.handlebars.internal.HbsParser.HashValueContext;
import com.github.jknack.handlebars.internal.HbsParser.NewlineContext;
import com.github.jknack.handlebars.internal.HbsParser.ParamContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialContext;
import com.github.jknack.handlebars.internal.HbsParser.SpacesContext;
import com.github.jknack.handlebars.internal.HbsParser.StatementContext;
import com.github.jknack.handlebars.internal.HbsParser.TemplateContext;
import com.github.jknack.handlebars.internal.HbsParser.TextContext;
import com.github.jknack.handlebars.internal.HbsParser.TvarContext;
import com.github.jknack.handlebars.internal.HbsParser.UnlessContext;
import com.github.jknack.handlebars.internal.HbsParser.VarContext;
import com.github.jknack.handlebars.internal.Variable.Type;

/**
 * Traverse the parse tree and build templates.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
abstract class TemplateBuilder extends HbsParserBaseVisitor<BaseTemplate> {

  /**
   * A handlebars object. required.
   */
  private Handlebars handlebars;

  /**
   * The file's name. required.
   */
  private String filename;

  /**
   * The partials registry.
   */
  private Map<String, Partial> partials;

  /**
   * Flag to track dead spaces and lines.
   */
  private Boolean hasTag;

  /**
   * Keep track of the current line.
   */
  protected StringBuilder line = new StringBuilder();

  /**
   * The stack trace.
   */
  protected LinkedList<Stacktrace> stacktraceList;

  /**
   * True if this is the root builder.
   */
  private boolean rootBuilder;

  /**
   * Creates a new {@link TemplateBuilder}.
   *
   * @param handlebars A handlbars object. required.
   * @param filename The file's name. required.
   * @param partials The partial registry. optional.
   * @param stacktraceList The stacktrace. optional.
   */
  public TemplateBuilder(final Handlebars handlebars, final String filename,
      final Map<String, Partial> partials,
      final LinkedList<Stacktrace> stacktraceList) {
    this.handlebars = notNull(handlebars, "The handlebars can't be null.");
    this.filename = notEmpty(filename, "The filename can't be empty/null.");
    this.partials = partials == null ? new HashMap<String, Partial>() : partials;
    this.stacktraceList = stacktraceList == null ? new LinkedList<Stacktrace>() : stacktraceList;
    this.rootBuilder = partials == null;
  }

  @Override
  public BaseTemplate visitBlock(final BlockContext ctx) {
    String nameStart = ctx.nameStart.getText();
    String nameEnd = ctx.nameEnd.getText();
    if (!nameStart.equals(nameEnd)) {
      reportError(null, ctx.nameEnd.getLine(), ctx.nameEnd.getCharPositionInLine()
          , String.format("found: '%s', expected: '%s'", nameEnd, nameStart));
    }

    hasTag(true);
    Block block = new Block(handlebars, nameStart, false, params(ctx.param()),
        hash(ctx.hash()));
    block.filename(filename);
    block.position(ctx.nameStart.getLine(), ctx.nameStart.getCharPositionInLine());
    String startDelim = ctx.start.getText();
    block.startDelimiter(startDelim.substring(0, startDelim.length() - 1));
    block.endDelimiter(ctx.stop.getText());

    BaseTemplate body = visitBody(ctx.thenBody);
    block.body(body);
    if (ctx.unlessBody != null) {
      BaseTemplate unless = visitBody(ctx.unlessBody);
      block.inverse(unless);
    }
    hasTag(true);
    return block;
  }

  @Override
  public BaseTemplate visitUnless(final UnlessContext ctx) {
    hasTag(true);
    Block block = new Block(handlebars, ctx.nameStart.getText(), true, Collections.emptyList(),
        Collections.<String, Object> emptyMap());
    block.filename(filename);
    block.position(ctx.nameStart.getLine(), ctx.nameStart.getCharPositionInLine());
    String startDelim = ctx.start.getText();
    block.startDelimiter(startDelim.substring(0, startDelim.length() - 1));
    block.endDelimiter(ctx.stop.getText());

    BaseTemplate body = visitBody(ctx.body());
    block.body(body);
    hasTag(true);
    return block;
  }

  @Override
  public BaseTemplate visitVar(final VarContext ctx) {
    hasTag(false);
    return newVar(ctx.QID().getSymbol(), Type.VAR, params(ctx.param()), hash(ctx.hash()),
        ctx.start.getText(), ctx.stop.getText());
  }

  @Override
  public BaseTemplate visitTvar(final TvarContext ctx) {
    hasTag(false);
    return newVar(ctx.QID().getSymbol(), Type.TRIPLE_VAR, params(ctx.param()), hash(ctx.hash()),
        ctx.start.getText(), ctx.stop.getText());
  }

  @Override
  public BaseTemplate visitAmpvar(final AmpvarContext ctx) {
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
  private BaseTemplate newVar(final Token name, final Type varType, final List<Object> params,
      final Map<String, Object> hash, final String startDelimiter, final String endDelimiter) {
    String varName = name.getText();
    Helper<Object> helper = handlebars.helper(varName);
    if (helper == null && (params.size() > 0 || hash.size() > 0)) {
      Helper<Object> helperMissing =
          handlebars.helper(Handlebars.HELPER_MISSING);
      if (helperMissing == null) {
        reportError((CommonToken) name, "could not find helper: '" + varName + "'");
      }
    }
    return new Variable(handlebars, varName, varType, params, hash)
        .startDelimiter(startDelimiter)
        .endDelimiter(endDelimiter)
        .filename(filename)
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
      result.put(hc.QID().getText(), hash(hc.hashValue()));
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
      result.add(param(param));
    }
    return result;
  }

  /**
   * Deserialize a param value.
   *
   * @param param A param.
   * @return A param value.
   */
  private Object param(final ParamContext param) {
    TerminalNode node = param.BOOLEAN();
    // boolean
    if (node != null) {
      return Boolean.valueOf(node.getText());
    }
    node = param.DOUBLE_STRING();
    if (node != null) {
      return node.getText().replace("\\\"", "\"");
    }
    node = param.INT();
    if (node != null) {
      return Integer.parseInt(node.getText());
    }
    return param.QID().getText();
  }

  /**
   * Deserialize a hash value.
   *
   * @param ctx A hash.
   * @return A hash value.
   */
  private Object hash(final HashValueContext ctx) {
    TerminalNode node = ctx.BOOLEAN();
    // boolean
    if (node != null) {
      return Boolean.valueOf(node.getText());
    }
    node = ctx.DOUBLE_STRING();
    if (node != null) {
      return node.getText().replace("\\\"", "\"");
    }
    node = ctx.SINGLE_STRING();
    if (node != null) {
      return node.getText().replace("\\\'", "\'");
    }
    node = ctx.INT();
    if (node != null) {
      return Integer.parseInt(node.getText());
    }
    return ctx.QID().getText();
  }

  @Override
  public BaseTemplate visitTemplate(final TemplateContext ctx) {
    BaseTemplate template = visitBody(ctx.body());
    this.handlebars = null;
    this.filename = null;
    this.hasTag = null;
    this.line.delete(0, line.length());
    this.line = null;
    if (rootBuilder) {
      this.partials.clear();
      this.stacktraceList.clear();
    }
    this.partials = null;
    this.stacktraceList = null;
    return template;
  }

  @Override
  public BaseTemplate visitPartial(final PartialContext ctx) {
    hasTag(true);
    Token pathToken = ctx.PATH().getSymbol();
    String uri = pathToken.getText();
    if (uri.startsWith("[") && uri.endsWith("]")) {
      uri = uri.substring(1, uri.length() - 1);
    }
    TemplateLoader loader = handlebars.getTemplateLoader();
    if (uri.startsWith("/")) {
      String message = "found: '/', partial shouldn't start with '/'";
      reportError(null, pathToken.getLine(), pathToken.getCharPositionInLine(), message);
    }
    String partialPath = loader.resolve(uri);
    if (!handlebars.allowInfiniteLoops() && isInStack(stacktraceList, partialPath)) {
      Collections.reverse(stacktraceList);
      String message = String.format(
          "an infinite loop was detected, partial '%s' was previously loaded\n%s", partialPath,
          join(stacktraceList, "\n"));
      reportError(null, pathToken.getLine(), pathToken.getCharPositionInLine(), message);
    }

    Partial partial = partials.get(partialPath);
    if (partial == null) {
      try {
        Stacktrace stacktrace =
            new Stacktrace(pathToken.getLine(), pathToken.getCharPositionInLine(), uri,
                filename);
        stacktraceList.addLast(stacktrace);
        String input = loader.loadAsString(URI.create(uri));

        HbsParserFactory parserFactory = (HbsParserFactory) handlebars.getParserFactory();
        Parser parser = parserFactory
            .create(handlebars, uri, "{{", "}}", partials, stacktraceList);
        String startDelim = ctx.start.getText();
        partial = new Partial().startDelimiter(startDelim.substring(0, startDelim.length() - 1))
            .endDelimiter(ctx.stop.getText());

        // Avoid stack overflow exceptions
        partials.put(partialPath, partial);

        Template template = parser.parse(partialInput(input, hasTag() ? line.toString() : null));
        TerminalNode partialContext = ctx.QID();
        partial.template(uri, template, partialContext != null ? partialContext.getText() : null);
        stacktraceList.removeLast();
      } catch (IOException ex) {
        String message = "The partial '" + partialPath + "' could not be found";
        reportError(null, pathToken.getLine(), pathToken.getCharPositionInLine(), message);
      }
    }
    return partial;
  }

  /**
   * True, if the file was already processed.
   *
   * @param stacktrace The current stack trace.
   * @param filename The filename to check for.
   * @return True, if the file was already processed.
   */
  private boolean isInStack(final LinkedList<Stacktrace> stacktrace, final String filename) {
    for (Stacktrace st : stacktrace) {
      if (st.getFilename().equals(filename)) {
        return true;
      }
    }
    return this.filename.equals(filename);
  }

  /**
   * Apply the given indent to the start of each line if necessary.
   *
   * @param input The whole input.
   * @param indent The indent to apply.
   * @return A new input.
   */
  private String partialInput(final String input, final String indent) {
    if (isEmpty(indent) || !isEmpty(indent.trim())) {
      return input;
    }
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

  @Override
  public BaseTemplate visitBody(final BodyContext ctx) {
    TemplateList list = new TemplateList();
    for (StatementContext statement : ctx.statement()) {
      BaseTemplate template = visit(statement);
      if (template != null) {
        list.add(template);
      }
    }
    return list;
  }

  @Override
  public BaseTemplate visitStatement(final StatementContext ctx) {
    if (ctx.spaces() != null) {
      return visitSpaces(ctx.spaces());
    }
    if (ctx.newline() != null) {
      return visitNewline(ctx.newline());
    }
    if (ctx.text() != null) {
      return visitText(ctx.text());
    }
    if (ctx.var() != null) {
      return visitVar(ctx.var());
    }
    if (ctx.tvar() != null) {
      return visitTvar(ctx.tvar());
    }
    if (ctx.ampvar() != null) {
      return visitAmpvar(ctx.ampvar());
    }
    if (ctx.block() != null) {
      return visitBlock(ctx.block());
    }
    if (ctx.partial() != null) {
      return visitPartial(ctx.partial());
    }
    if (ctx.unless() != null) {
      return visitUnless(ctx.unless());
    }
    if (ctx.delimiters() != null) {
      hasTag(true);
      return null;
    }
    if (ctx.comment() != null) {
      hasTag(true);
      return null;
    }
    throw new IllegalStateException();
  }

  @Override
  public BaseTemplate visitText(final TextContext ctx) {
    String text = ctx.getText();
    line.append(text);
    return new Text(text);
  }

  @Override
  public BaseTemplate visitSpaces(final SpacesContext ctx) {
    Token space = ctx.SPACE().getSymbol();
    String text = space.getText();
    line.append(text);
    if (space.getChannel() == Token.HIDDEN_CHANNEL) {
      return null;
    }
    return new Blank(text);
  }

  @Override
  public BaseTemplate visitNewline(final NewlineContext ctx) {
    Token newline = ctx.NL().getSymbol();
    if (newline.getChannel() == Token.HIDDEN_CHANNEL) {
      return null;
    }
    String text = newline.getText();
    line.setLength(0);
    return new Blank(text);
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
