/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.internal.HbsParser.AmpvarContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockContext;
import com.github.jknack.handlebars.internal.HbsParser.CommentContext;
import com.github.jknack.handlebars.internal.HbsParser.DelimitersContext;
import com.github.jknack.handlebars.internal.HbsParser.ElseStmtChainContext;
import com.github.jknack.handlebars.internal.HbsParser.ElseStmtContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialBlockContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialContext;
import com.github.jknack.handlebars.internal.HbsParser.RawBlockContext;
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

  /** The token stream. */
  private CommonTokenStream tokens;

  /**
   * Constructor.
   *
   * @param tokens The token stream.
   */
  public WhiteSpaceControl(final CommonTokenStream tokens) {
    this.tokens = tokens;
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
    trim(ctx.START(1).getSymbol(), ctx.END(1).getSymbol());
  }

  @Override
  public void enterRawBlock(final RawBlockContext ctx) {
    trim(ctx.start, ctx.END_RAW_BLOCK(0).getSymbol());
  }

  @Override
  public void exitRawBlock(final RawBlockContext ctx) {
    trim(ctx.START_RAW_BLOCK(1).getSymbol(), ctx.END_RAW_BLOCK(1).getSymbol());
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
  public void enterPartialBlock(final PartialBlockContext ctx) {
    trim(ctx.start, ctx.END(0).getSymbol());
  }

  @Override
  public void exitPartialBlock(final PartialBlockContext ctx) {
    trim(ctx.START(1).getSymbol(), ctx.END(1).getSymbol());
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
  public void exitUnless(final UnlessContext ctx) {
    trim(ctx.START(1).getSymbol(), ctx.END(1).getSymbol());
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
   * Trim on left/right where required.
   *
   * @param startToken The start token.
   * @param endToken The end token.
   */
  private void trim(final Token startToken, final Token endToken) {
    int startIndex = startToken.getTokenIndex();
    int endIndex = endToken.getTokenIndex();
    boolean trimLeft =
        text(startToken).contains("~")
            || (startIndex + 1 < tokens.size()
                && tokens.get(startIndex + 1).getType() == HbsLexer.WS_CONTROL);
    if (trimLeft) {
      if (startIndex > 0) {
        CommonToken leftToken = (CommonToken) tokens.get(startIndex - 1);
        if (leftToken != null && leftToken.getType() == HbsLexer.TEXT) {
          String trimmed = StringUtils.stripEnd(leftToken.getText(), null);
          leftToken.setText(trimmed);
        }
      }
    }

    boolean trimRight =
        text(endToken).contains("~")
            || (endIndex > 0 && tokens.get(endIndex - 1).getType() == HbsLexer.WS_CONTROL);
    if (trimRight) {
      CommonToken rightToken = (CommonToken) tokens.get(endIndex + 1);
      if (rightToken != null && rightToken.getType() == HbsLexer.TEXT) {
        String trimmed = StringUtils.stripStart(rightToken.getText(), null);
        rightToken.setText(trimmed);
      }
    }
  }

  /**
   * @param token The candidate token.
   * @return Text of the candidate token.
   */
  private String text(final Token token) {
    return token.getText();
  }
}
