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
import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.internal.HbsParser.AmpvarContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockContext;
import com.github.jknack.handlebars.internal.HbsParser.CommentContext;
import com.github.jknack.handlebars.internal.HbsParser.DelimitersContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialContext;
import com.github.jknack.handlebars.internal.HbsParser.TemplateContext;
import com.github.jknack.handlebars.internal.HbsParser.TextContext;
import com.github.jknack.handlebars.internal.HbsParser.TvarContext;
import com.github.jknack.handlebars.internal.HbsParser.UnlessContext;
import com.github.jknack.handlebars.internal.HbsParser.VarContext;

/**
 * Remove space and lines according to the Mustache Spec.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
public class MustacheSpec extends HbsParserBaseListener {

  /**
   * Tracks if the current line should be treated as stand-alone.
   */
  private Boolean standAlone;

  /**
   * Tracks text tokens for future whitespace removal.
   */
  private List<CommonToken> textTokens = new ArrayList<>();

  /**
   * Channel for tokens that need their last line removed.
   */
  public static final int REMOVE_LAST_LINE_CHANNEL = Token.MIN_USER_CHANNEL_VALUE;

  @Override
  public void exitTemplate(final TemplateContext ctx) {
    removeWhitespace();
    this.textTokens.clear();
    this.standAlone = null;
  }

  @Override
  public void enterText(final TextContext ctx) {
    CommonToken currentToken = (CommonToken) ctx.getStart();
    String currentText = currentToken.getText();
    Integer secondLineIndex = MustacheStringUtils.indexOfSecondLine(currentText);
    if (secondLineIndex == null) {
      // Non-whitespace was found. Clear list and start again with the current token
      this.textTokens.clear();
      this.standAlone = null;
    } else if (secondLineIndex >= 0) {
      // Try to remove whitespace from the previously saved text tokens
      boolean canRemoveWhitespace = removeWhitespace();
      if (canRemoveWhitespace) {
        // Remove the first line of this text as well
        String newText = StringUtils.substring(currentText, secondLineIndex);
        currentToken.setText(newText);
      }

      // Clear list and start again with the current token
      this.textTokens.clear();
      this.standAlone = null;
    }

    this.textTokens.add(currentToken);
  }

  @Override
  public void enterBlock(final BlockContext ctx) {
    hasTag(true);
  }

  @Override
  public void exitBlock(final BlockContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterComment(final CommentContext ctx) {
    hasTag(true);
  }

  @Override
  public void exitPartial(final PartialContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterDelimiters(final DelimitersContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterUnless(final UnlessContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterElseBlock(final HbsParser.ElseBlockContext ctx) {
    hasTag(true);
  }

  @Override
  public void exitUnless(final UnlessContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterAmpvar(final AmpvarContext ctx) {
    hasTag(false);
  }

  @Override
  public void enterTvar(final TvarContext ctx) {
    hasTag(false);
  }

  @Override
  public void enterVar(final VarContext ctx) {
    hasTag(false);
  }

  /**
   * Mark the current line with a mustache instruction.
   *
   * @param hasTag True, to indicate there is a mustache instruction.
   */
  private void hasTag(final boolean hasTag) {
    if (this.standAlone != Boolean.FALSE) {
      this.standAlone = hasTag;
    }
  }

  /**
   * Remove whitespace from previously saved text tokens.
   *
   * @return True if whitespace could be removed. False otherwise.
   */
  private boolean removeWhitespace() {
    boolean canRemoveWhitespace = this.standAlone == null ? false : this.standAlone.booleanValue();
    if (!canRemoveWhitespace) {
      return false;
    }

    if (textTokens.isEmpty()) {
      return true;
    }

    // Try to remove whitespace from the last line
    CommonToken lastToken = textTokens.get(textTokens.size() - 1);
    String lastText = lastToken.getText();
    int newlineIndex = StringUtils.lastIndexOfAny(lastText, "\r", "\n");
    if (newlineIndex >= 0) {
      String lastLine = lastText.substring(newlineIndex + 1);
      if (!StringUtils.isWhitespace(lastLine)) {
        // Cannot remove anything since line contains non-whitespace
        return false;
      }

      // Mark the last line for removal
      lastToken.setChannel(REMOVE_LAST_LINE_CHANNEL);
    } else {
      // Check for non-whitespace
      int maxIndex = textTokens.size() - 1;
      for (int i = maxIndex; i >= 0; i--) {
        CommonToken loopToken = textTokens.get(i);
        String loopText = loopToken.getText();
        if (!StringUtils.isWhitespace(loopText)) {
          // Cannot remove anything since line contains non-whitespace
          return false;
        }
        if (i == 0) {
          // Mark tokens already checked for removal
          int j = 0;
          while (j <= maxIndex) {
            CommonToken whiteSpaceToken = textTokens.get(j);
            whiteSpaceToken.setChannel(Token.HIDDEN_CHANNEL);
            j++;
          }

          break;
        }
      }
    }

    return true;
  }
}
