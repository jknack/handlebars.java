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

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

import com.github.jknack.handlebars.internal.HbsParser.AmpvarContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockContext;
import com.github.jknack.handlebars.internal.HbsParser.CommentContext;
import com.github.jknack.handlebars.internal.HbsParser.DelimitersContext;
import com.github.jknack.handlebars.internal.HbsParser.ElseStmtChainContext;
import com.github.jknack.handlebars.internal.HbsParser.ElseStmtContext;
import com.github.jknack.handlebars.internal.HbsParser.NewlineContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialContext;
import com.github.jknack.handlebars.internal.HbsParser.SpacesContext;
import com.github.jknack.handlebars.internal.HbsParser.TemplateContext;
import com.github.jknack.handlebars.internal.HbsParser.TextContext;
import com.github.jknack.handlebars.internal.HbsParser.TvarContext;
import com.github.jknack.handlebars.internal.HbsParser.UnlessContext;
import com.github.jknack.handlebars.internal.HbsParser.VarContext;

/**
 * Implementation of white-space control. It trims output on left/right of mustache expressions or
 * text.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
public class WhiteSpaceControl extends HbsParserBaseListener {

  /**
   * Track the spaces/lines that need to be excluded.
   */
  private List<CommonToken> spaces = new ArrayList<CommonToken>();

  /** Greater than zero, if a trim-right operation is required. */
  private int pending = 0;

  @Override
  public void enterSpaces(final SpacesContext ctx) {
    CommonToken space = (CommonToken) ctx.SPACE().getSymbol();
    spaces.add(space);
  }

  @Override
  public void enterNewline(final NewlineContext ctx) {
    CommonToken newline = (CommonToken) ctx.NL().getSymbol();
    spaces.add(newline);
  }

  @Override
  public void exitTemplate(final TemplateContext ctx) {
    trimRight();
  }

  /** Trim-left operation. */
  private void trimLeft() {
    hideSpaces();
  }

  /** Move space tokens to the hidden channel. */
  private void hideSpaces() {
    for (CommonToken space : spaces) {
      space.setChannel(Token.HIDDEN_CHANNEL);
    }
  }

  /** Trim-right, if ONLY if pending > 0. */
  private void trimRight() {
    if (pending > 0) {
      hideSpaces();
      pending -= 1;
    }
  }

  @Override
  public void enterText(final TextContext ctx) {
    trim(ctx.start, ctx.stop);
  }

  @Override
  public void enterBlock(final BlockContext ctx) {
    trim(ctx.start, ctx.END(0).getSymbol());
  }

  @Override
  public void enterElseStmt(final ElseStmtContext ctx) {
    trim(ctx.start, ctx.END().getSymbol());
  }

  @Override
  public void enterElseStmtChain(final ElseStmtChainContext ctx) {
    trim(ctx.start, ctx.END().getSymbol());
  }

  @Override
  public void exitBlock(final BlockContext ctx) {
    trim(ctx.END_BLOCK().getSymbol(), ctx.END(1).getSymbol());
  }

  @Override
  public void enterComment(final CommentContext ctx) {
    trim(ctx.start, ctx.stop);
  }

  @Override
  public void enterPartial(final PartialContext ctx) {
    trim(ctx.start, ctx.stop);
  }

  @Override
  public void enterDelimiters(final DelimitersContext ctx) {
    trim(ctx.start, ctx.stop);
  }

  @Override
  public void enterUnless(final UnlessContext ctx) {
    trim(ctx.start, ctx.END().get(0).getSymbol());
  }

  @Override
  public void enterAmpvar(final AmpvarContext ctx) {
    trim(ctx.start, ctx.stop);
  }

  @Override
  public void enterTvar(final TvarContext ctx) {
    trim(ctx.start, ctx.stop);
  }

  @Override
  public void enterVar(final VarContext ctx) {
    trim(ctx.start, ctx.stop);
  }

  /**
   * Trim on left/right is required.
   *
   * @param startToken The start token.
   * @param endToken The end token.
   */
  private void trim(final Token startToken, final Token endToken) {
    trimRight();

    String start = text(startToken);
    if (start.indexOf("~") > 0) {
      trimLeft();
    }

    String end = text(endToken);
    if (end.indexOf("~") == 0) {
      pending += 1;
    }

    // clear tokens
    spaces.clear();
  }

  /**
   * @param token The candidate token.
   * @return Text of the candidate token.
   */
  private String text(final Token token) {
    return token.getText();
  }
}
